package com.minivision.fdi.service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import com.minivision.fdi.domain.FaceRecMsg;
import com.minivision.fdi.domain.QrRecMsg;
import com.minivision.fdi.faceplat.result.detect.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.minivision.ai.util.ChunkRequest;
import com.minivision.fastdfs.FdfsService;
import com.minivision.fdi.common.ImageUtils;
import com.minivision.fdi.entity.Face;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.event.FaceAddEvent;
import com.minivision.fdi.event.FaceRemoveEvent;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.faceplat.client.FacePlatClient;
import com.minivision.fdi.faceplat.ex.FacePlatException;
import com.minivision.fdi.faceplat.result.detect.DetectResult;
import com.minivision.fdi.faceplat.result.detect.DetectedFace;
import com.minivision.fdi.faceplat.result.detect.DetectedFace.Rectangle;
import com.minivision.fdi.faceplat.result.detect.faceset.AddFaceResult;
import com.minivision.fdi.faceplat.result.detect.faceset.RemoveFaceResult;
import com.minivision.fdi.repository.FaceRepository;
import com.minivision.fdi.rest.param.FaceAddParam;
import com.minivision.fdi.rest.param.FaceDelParam;
import com.minivision.fdi.rest.param.FaceParam;
import com.minivision.fdi.rest.param.FaceUpdateParam;
import com.minivision.fdi.rest.result.FaceAddResult;
import com.minivision.fdi.rest.result.FaceDelResult;
import com.minivision.fdi.rest.result.FaceUpdateResult;
import com.minivision.fdi.rest.result.common.FailureDetail;
import com.minivision.fdi.rest.result.common.PageResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class FaceServiceImpl implements FaceService, ApplicationEventPublisherAware {

    @Autowired
    private FaceRepository faceRepository;

    @Autowired
    private FacePlatClient facePlatClient;
    
    @Autowired
    private FdfsService fdfsService;

    private ApplicationEventPublisher publisher;

    @Value("${threshold:0.80}")
    private float threshold;

    @Override public PageResult<Face> findFaceInfos(FaceParam faceParam) {
            Pageable page = new ChunkRequest(faceParam.getOffset(),faceParam.getLimit());
            Page<Face> faces = faceRepository.findAll((root, query, cb) -> {
                Path<Meeting> meet = root.get("meeting");
                Predicate predicate =null;
                if (StringUtils.isNotBlank(faceParam.getName())) {
                    Predicate name = cb.like(root.get("name"), "%" + faceParam.getName() + "%");
                    predicate = cb.and(name);
                }
                if (StringUtils.isNotBlank(faceParam.getPhoneNumber())) {
                    Predicate phoneNumber = cb.like(root.get("phoneNumber"),
                        "%" + faceParam.getPhoneNumber() + "%");
                    if(Optional.ofNullable(predicate).isPresent()) {
                        predicate = cb.and(predicate,phoneNumber);
                    }
                    else predicate  = cb.and(phoneNumber);
                }
                if (StringUtils.isNotBlank(faceParam.getCompanyName())) {
                    Predicate company = cb.like(root.get("companyName"),
                        "%" + faceParam.getCompanyName() + "%");
                    if(Optional.ofNullable(predicate).isPresent()) {
                        predicate = cb.and(predicate,company);
                    }
                    else predicate = cb.and(company);
                }
                if (StringUtils.isNotBlank(faceParam.getMeetName())) {
                    Predicate meetName = cb.like(meet.get("name"), "%"+faceParam.getMeetName()+"%");
                    if(Optional.ofNullable(predicate).isPresent()) {
                        predicate = cb.and(predicate, meetName);
                    }
                    else predicate = cb.and(meetName);
                }
                if(predicate != null) {
                    query.where(predicate);
                }
                return query.getRestriction();
            },page);
            return new PageResult<>(faces.getTotalElements(),faces.getContent());
        }

    @Override public FaceAddResult addFace(FaceAddParam faceAddParam) throws ServiceException{

        try {
            Assert.notNull(faceAddParam.getMeetToken(),"meetToken must not be null");
            if(faceAddParam.getImgFile() == null && faceAddParam.getQrCode() ==null){
                throw new ServiceException(500,"img or qrCode at least one needed!" );
            }
            if(faceAddParam.getQrCode() != null){
                Face face = faceRepository.findByQrCodeAndMeetingToken(faceAddParam.getQrCode(),faceAddParam.getMeetToken());
                Assert.isTrue(face == null,"qrCode and meeting can not duplicate at same time!");
            }
            Face face = new Face();
            face.setId(UUID.randomUUID().toString());
            BeanUtils.copyProperties(faceAddParam, face);
            String meetToken = faceAddParam.getMeetToken();
            face.setMeeting(new Meeting(meetToken));
            if(faceAddParam.getImgFile() != null) {
                //register with img
                byte[] imgData = faceAddParam.getImgFile().getBytes();
                DetectedFace detectedFace = detect(imgData);
                Rectangle rectangle = detectedFace.getFaceRectangle();
                String faceToken = detectedFace.getFaceToken();
                face.setFaceToken(faceToken);
                AddFaceResult addFaceResult = saveFace(rectangle,imgData, ".jpg", face);
            }
            Face created = faceRepository.saveAndFlush(face);
            publisher.publishEvent(new FaceAddEvent(this, created));
            return new FaceAddResult(face.getId(),1);
        }
        catch (IOException e){
            throw new ServiceException(500,e.getMessage());
        }
        catch (Exception e){
            throw new ServiceException(e);
        }
     }

    private AddFaceResult saveFace(Rectangle rectangle, byte[] imgData, String fileType, Face face)
        throws Exception{
        //AddFaceResult addFaceResult = new AddFaceResult();
        AddFaceResult addFaceResult = facePlatClient.addFace( face.getMeeting().getToken(), face.getFaceToken());
        if (addFaceResult.getFailureDetail() != null) {
            FailureDetail failureDetail = addFaceResult.getFailureDetail().get(0);
            log.warn("add face[{}] to meeting[{}] fail, {}", failureDetail.getFaceToken(),
                face.getMeeting().getToken(), failureDetail.getReason());
            throw new FacePlatException(500, "add face to meeting failed, " + failureDetail.getReason());
        }
        //enlarge img
        BufferedImage bi =   ImageUtils.getBufferedImage(imgData);
        BufferedImage subImage =  ImageUtils.enlarge(bi,rectangle.getLeft(), rectangle.getTop(), rectangle.getWidth(),rectangle.getHeight());
        imgData = ImageUtils.writeImageToBytes(subImage,fileType.substring(1));
        //save img
        String fileName = face.getMeeting().getToken() + File.separator + face.getId() + fileType;

        face.setImgPath(fdfsService.getFileUrl(fdfsService.uploadToFastDFS(fileName, imgData)));

        return addFaceResult;
    }

    @Override public FaceUpdateResult update(FaceUpdateParam faceUpdateParam) throws ServiceException {
        Face face = faceRepository.findOne(faceUpdateParam.getId());

        try {
            if (faceUpdateParam.getImgFile() != null) {
                    byte[] imgData = faceUpdateParam.getImgFile().getBytes();
                    DetectedFace detectedFace = detect(imgData);
                    //face register with pic
                    if(face.getFaceToken() != null) {
                        RemoveFaceResult removeFaceResult = facePlatClient.removeFace(face.getMeeting().getToken(), face.getFaceToken());
                        if (removeFaceResult.getFailureDetail() != null) {
                                throw new FacePlatException(500,removeFaceResult.getFailureDetail().get(0).toString());
                        }
                    }
                   face.setFaceToken(detectedFace.getFaceToken());
                   saveFace(detectedFace.getFaceRectangle(), imgData, ".jpg", face);
            }
            if(faceUpdateParam.getQrCode() != null){
                Face newFace = faceRepository.findByQrCodeAndMeetingToken(faceUpdateParam.getQrCode(),face.getMeeting().getToken());
                if(newFace !=null){
                    if(!face.getId().equals(newFace.getId())){
                        throw new ServiceException(500,"qrCode and meeting can not duplicate at same time!");
                    }
                }
                face.setQrCode(faceUpdateParam.getQrCode());
            }
            face.setName(Optional.ofNullable(faceUpdateParam.getName()).orElse(face.getName()));
            face.setVip(Optional.ofNullable(faceUpdateParam.getVip()).orElse(face.getVip()));
            face.setCompanyName(Optional.ofNullable(faceUpdateParam.getCompanyName()).orElse(face.getCompanyName()));
            face.setPosition(Optional.ofNullable(faceUpdateParam.getPosition()).orElse(face.getPosition()));
            face.setIdCard(Optional.ofNullable(faceUpdateParam.getIdCard()).orElse(face.getIdCard()));
            face.setPhoneNumber(Optional.ofNullable(faceUpdateParam.getPhoneNumber()).orElse(face.getPhoneNumber()));
            face.setReserveFir(Optional.ofNullable(faceUpdateParam.getReserveFir()).orElse(face.getReserveFir()));
            face.setReserveSec(Optional.ofNullable(faceUpdateParam.getReserveSec()).orElse(face.getReserveSec()));
            face.setReserveThi(Optional.ofNullable(faceUpdateParam.getReserveThi()).orElse(face.getReserveThi()));
            faceRepository.save(face);
            return new FaceUpdateResult(1);
        }catch (IOException e){
            throw new ServiceException(500,e.getMessage());
        }
        catch (Exception e){
            throw new ServiceException(e);
        }
    }

    @Override public FaceDelResult delete(FaceDelParam faceDelParam) throws ServiceException{
        try {
            FaceDelResult faceDelResult = new FaceDelResult();
            String ids = faceDelParam.getIds();

            List<String> idList = Arrays.stream(ids.split(",")).collect(Collectors.toList());
            //all faces
            List<Face> faces = faceRepository.findByIdIn(idList);
            //will be deleted in facePlat
            List<String> faceTokens = faces.stream().filter(face -> face.getFaceToken()!=null).map(Face::getFaceToken).collect(Collectors.toList());
            RemoveFaceResult removeFaceResult = null;
            if(!faceTokens.isEmpty()) {
                String tokens = StringUtils.join(faceTokens, ",");
                removeFaceResult = facePlatClient.removeFace(faceDelParam.getMeetToken(), tokens);
//                BeanUtils.copyProperties(removeFaceResult, faceDelResult);
            }
            faceRepository.deleteInBatch(faces);
            publisher.publishEvent(new FaceRemoveEvent(this, faceDelParam.getMeetToken(), faces.size()));
            faceDelResult = new FaceDelResult(faces.size());
            faceDelResult.setFailureDetail(Optional.ofNullable(removeFaceResult).map(RemoveFaceResult::getFailureDetail).orElse(null));
            return faceDelResult;
        }catch (Exception e){
            throw new ServiceException(e);
        }

    }

    @Override public Face search(FaceRecMsg faceRecMsg) throws RuntimeException{
           //TODO try
            SearchResult searchResult = facePlatClient
                .searchByFeature(faceRecMsg.getMeetingId(), faceRecMsg.getFeature(), 1);
            if (!CollectionUtils.isEmpty(searchResult.getResults())) {
                if (searchResult.getResults().get(0).getConfidence() >= threshold) {
                    return faceRepository.findByFaceToken(searchResult.getResults().get(0).getFaceToken());
                }
            }
        return null;
    }

    @Override public Face searchByQrCode(QrRecMsg regMsg) {
        Face face = faceRepository.findByQrCodeAndMeetingToken(regMsg.getQrCode(), regMsg.getMeetingId());
        return  face;
    }

    DetectedFace detect(byte[] imgData) throws FacePlatException {
        DetectResult detectResult  = facePlatClient.detect(imgData, true);
        List<DetectedFace> faces = detectResult.getFaces();
        if (faces.isEmpty()) {
            throw new FacePlatException(1, "no face detected");
        }
        DetectedFace detectedFace = faces.get(0);
        return  detectedFace;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
      this.publisher = applicationEventPublisher;
    }
}
