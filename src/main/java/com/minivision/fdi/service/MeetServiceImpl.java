package com.minivision.fdi.service;

import java.util.Date;
import java.util.Optional;

import javax.persistence.criteria.Predicate;

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

import com.hazelcast.util.UuidUtil;
import com.minivision.ai.util.ChunkRequest;
import com.minivision.fdi.domain.MeetingStatus;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.event.MeetingAddEvent;
import com.minivision.fdi.event.MeetingRemoveEvent;
import com.minivision.fdi.event.MeetingUpdateEvent;
import com.minivision.fdi.exception.ServiceException;
import com.minivision.fdi.faceplat.client.FacePlatClient;
import com.minivision.fdi.faceplat.ex.FacePlatException;
import com.minivision.fdi.faceplat.result.detect.faceset.SetCreateResult;
import com.minivision.fdi.faceplat.result.detect.faceset.SetDeleteResult;
import com.minivision.fdi.repository.MeetRepository;
import com.minivision.fdi.rest.param.MeetAddParam;
import com.minivision.fdi.rest.param.MeetParam;
import com.minivision.fdi.rest.param.MeetUpdateParam;
import com.minivision.fdi.rest.result.MeetingAddResult;
import com.minivision.fdi.rest.result.MeetingUpdateResult;
import com.minivision.fdi.rest.result.common.PageResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class MeetServiceImpl implements MeetService, ApplicationEventPublisherAware {

    @Autowired
    private FacePlatClient facePlatClient;

    @Autowired MeetRepository meetRepository;
    
    private ApplicationEventPublisher publisher;

    @Override public MeetingAddResult addMeeting(MeetAddParam meetAddParam) throws ServiceException{
        try {
            Assert.isTrue(meetAddParam.getEndTime() > meetAddParam.getStartTime(),"endTime must be greater than startTime");
            Meeting meeting = new Meeting();
//            meeting.setUpdateTime(new Date());
            BeanUtils.copyProperties(meetAddParam,meeting);
            SetCreateResult setCreateResult = facePlatClient.createFaceset(meeting);
            if (setCreateResult != null && setCreateResult.getFacesetToken() != null) {
                meeting.setToken(setCreateResult.getFacesetToken());
                Meeting created = meetRepository.save(meeting);
                publisher.publishEvent(new MeetingAddEvent(this, created));
                return new MeetingAddResult(meeting.getToken());
            }
            else {
                //TODO EXCEPTION
                throw new ServiceException("unknown exception");
            }
        }catch (Exception e){
            log.error("add meeting failed,catch exception {}",e);
            throw new ServiceException(e);
        }
    }

    @Override public PageResult<Meeting> findByPlat(MeetParam meetParam) {
        Pageable page = new ChunkRequest(meetParam.getOffset(),meetParam.getLimit());
        Page<Meeting> meets = meetRepository.findAll((root, query, cb) -> {
            Predicate predicate;
            Predicate status = null;
            long now = new Date().getTime();
            if(meetParam.getStatus() == null){
                  status = cb.greaterThanOrEqualTo(root.get("startTime"),now);
            }
            else
            switch (meetParam.getStatus()){
                case NOT_STARTED:
                    status = cb.greaterThanOrEqualTo(root.get("startTime"),now);
                    break;
                case STARTING:
                    Predicate less =  cb.greaterThanOrEqualTo(root.get("endTime"),now);
                    Predicate greater = cb.lessThanOrEqualTo(root.get("startTime"),now);
                    status = cb.and(less,greater);
                    break;
                case ENDED:
                    status = cb.lessThanOrEqualTo(root.get("endTime"),now);
                    break;
                default:
                    status = cb.greaterThanOrEqualTo(root.get("startTime"),now);
            }
            predicate = cb.and(status);

            if(meetParam.getStartTime() != null){
                Predicate startTime =  cb.greaterThanOrEqualTo(root.get("startTime"),meetParam.getStartTime());
                predicate = cb.and(predicate,startTime);
            }
            if(meetParam.getEndTime() != null){
                Predicate endTime =  cb.lessThanOrEqualTo(root.get("endTime"),meetParam.getEndTime());
                predicate = cb.and(predicate,endTime);
            }
            if (StringUtils.isNotBlank(meetParam.getName())) {
                Predicate name = cb.like(root.get("name"), "%" + meetParam.getName() + "%");
                 predicate = cb.and(predicate,name);
            }
            if (StringUtils.isNotBlank(meetParam.getAddress())) {
                Predicate address = cb.like(root.get("address"),
                    "%" + meetParam.getAddress() + "%");
                  predicate = cb.and(predicate,address);
            }
            return  query.where(predicate).getRestriction();
        },page);

        for (Meeting meeting : meets.getContent()){
            meeting.setFaceCount(meeting.getFaces() == null ? 0 : meeting.getFaces().size());
            if (meetParam.getStatus() != null) {
                meeting.setStatus(meetParam.getStatus());
            } else {
                long now = new Date().getTime();
                if (meeting.getStartTime() > now)
                    meeting.setStatus(MeetingStatus.NOT_STARTED);
                else if (meeting.getStartTime() < now && meeting.getEndTime() > now)
                    meeting.setStatus(MeetingStatus.STARTING);
                else if (meeting.getEndTime() < now)
                    meeting.setStatus(MeetingStatus.ENDED);
            }
        }

        return new PageResult<>(meets.getTotalElements(),meets.getContent());
    }

    @Override public MeetingUpdateResult update(MeetUpdateParam updateParam) throws ServiceException{
        Assert.notNull(updateParam.getToken(),"meet token must not be null");
        Meeting meeting = meetRepository.findOne(updateParam.getToken());
        Assert.notNull(meeting,"meeting not found,check token please");
        meeting.setName(Optional.ofNullable(updateParam.getName()).orElse(meeting.getName()));
        meeting.setAddress(Optional.ofNullable(updateParam.getAddress()).orElse(meeting.getAddress()));
        meeting.setStartTime(Optional.ofNullable(updateParam.getStartTime()).orElse(meeting.getStartTime()));
        meeting.setEndTime(Optional.ofNullable(updateParam.getEndTime()).orElse(meeting.getEndTime()));
        meeting.setDeadline(Optional.ofNullable(updateParam.getDeadline()).orElse(meeting.getDeadline()));
        meeting.setVenue(Optional.ofNullable(updateParam.getVenue()).orElse(meeting.getVenue()));
        try {
            Meeting updated = meetRepository.save(meeting);
            publisher.publishEvent(new MeetingUpdateEvent(this, updated));
//            SetModifyResult setModifyResult = facePlatClient.updateFaceset(meeting);
            //TODO RESULT
            return new MeetingUpdateResult(meeting.getToken());
        } catch (Exception e){
            throw new ServiceException(e);
        }
    }

    @Override public MeetingUpdateResult delete(String id) throws ServiceException{
        Assert.notNull(id,"faceSet token must not be null");
        SetDeleteResult deleteResult = null;
        try {
            Meeting removed = meetRepository.findOne(id);
            meetRepository.delete(removed);
            deleteResult = facePlatClient.delFaceset(id, true);
            publisher.publishEvent(new MeetingRemoveEvent(this, removed));
        }
        catch (Exception e){
            if(e instanceof FacePlatException){
                log.warn("force to delete faceSet{},catch  exception {}",id,e.getMessage());
            }
            else
                throw new ServiceException(e);
        }
        return new MeetingUpdateResult(id);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
      this.publisher = applicationEventPublisher;
    }
}
