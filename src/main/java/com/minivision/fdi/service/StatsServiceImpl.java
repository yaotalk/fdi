package com.minivision.fdi.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.minivision.ai.util.Base64;
import com.minivision.ai.util.ChunkRequest;
import com.minivision.fastdfs.FdfsService;
import com.minivision.fdi.entity.Stats;
import com.minivision.fdi.repository.MeetingManageRepository;
import com.minivision.fdi.repository.StatsRepository;
import com.minivision.fdi.rest.param.CreateStatsParam;
import com.minivision.fdi.rest.param.QueryStatsParam;
import com.minivision.fdi.rest.result.FaceRecognitionResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional(rollbackFor={Exception.class})
@Slf4j
public class StatsServiceImpl implements StatsService {

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private StatsRepository statsRepo;

  @Autowired
  private MeetingManageRepository meetingRepo;

  @Autowired
  private FdfsService fdfsService;
  
  private Stats convertParam(CreateStatsParam param) throws Exception {
    Stats record = new Stats();
    BeanUtils.copyProperties(param, record);
    record.setDetectTime(new Date(param.getTime()));
    if (StringUtils.hasText(param.getCapImg())) {
      byte[] capImgBytes = Base64.decode(param.getCapImg(), Base64.DEFAULT);
      record.setCapImgUrl(fdfsService.getFileUrl(fdfsService.uploadToFastDFS(param.getFaceId() + "_cap.jpg", capImgBytes)));
    }
    return record;
  }

  @Override
  public Stats createStats(CreateStatsParam stats) throws Exception {
    Stats created = statsRepo.saveAndFlush(convertParam(stats));
    if (created != null) {
      String meetingToken = stats.getMeetingToken();
      meetingRepo.updateAttendance(statsRepo.findDistinctByMeetingToken(meetingToken), meetingToken);
    }
    return created;
  }

  @Override
  public List<Stats> createBatch(List<CreateStatsParam> param) throws Exception {
    List<Stats> statsList = new ArrayList<>();
    for (CreateStatsParam createStatsParam : param) {
      statsList.add(convertParam(createStatsParam));
    }
    List<Stats> saved = statsRepo.save(statsList);
    if (statsList.get(0) != null && !CollectionUtils.isEmpty(saved)) {
      String meetingToken = statsList.get(0).getMeetingToken();
      meetingRepo.updateAttendance(statsRepo.findDistinctByMeetingToken(meetingToken), meetingToken);
    }
    return saved;
  }

  @Override
  public Page<FaceRecognitionResult> findByPage(QueryStatsParam param) {
    String baseSql = "select m.meeting_name,m.address,f.company_name,f.name,f.vip,f.img_path,s.cap_img_url,s.success,s.confidence,s.detect_time"
        + " from stats s,meeting m,face f where s.meeting_token = m.token and s.face_id = f.id";
    String querySql = baseSql;
    querySql += " and m.meeting_name like ?";
    querySql += " and f.company_name like ?";
    querySql += " and f.name like ?";
    long total = 0;
    String countSql = "select count(*) from (" + querySql + ") temp";
    String pageSql = querySql + " order by s.detect_time desc limit " + param.getOffset() + "," + param.getLimit();
    log.info(countSql);
    log.info(pageSql);
    Query countQuery = entityManager.createNativeQuery(countSql);
    Query pageQuery = entityManager.createNativeQuery(pageSql);
    countQuery.setParameter(1, "%" + Optional.ofNullable(param.getMeetingName()).orElse("") + "%");
    pageQuery.setParameter(1, "%" + Optional.ofNullable(param.getMeetingName()).orElse("") + "%");
    countQuery.setParameter(2, "%" + Optional.ofNullable(param.getCompanyName()).orElse("") + "%");
    pageQuery.setParameter(2, "%" + Optional.ofNullable(param.getCompanyName()).orElse("") + "%");
    countQuery.setParameter(3, "%" + Optional.ofNullable(param.getName()).orElse("") + "%");
    pageQuery.setParameter(3, "%" + Optional.ofNullable(param.getName()).orElse("") + "%");
    total = ((BigInteger) (countQuery.getSingleResult())).longValue();
    //pageQuery.unwrap(SQLQuery.class).setResultTransformer(Transformers.aliasToBean(FaceRecognitionResult.class));
    List<Object[]> list = pageQuery.getResultList();
    List<FaceRecognitionResult> resultList = new ArrayList<>();
    for (Object[] values : list) {
      FaceRecognitionResult recognition = new FaceRecognitionResult();
      recognition.setMeetingName(values[0] == null ? "" : (String) values[0]);
      recognition.setAddress(values[1] == null ? "" : (String) values[1]);
      recognition.setCompanyName(values[2] == null ? "" : (String) values[2]);
      recognition.setName(values[3] == null ? "" : (String) values[3]);
      recognition.setVip(values[4] == null ? null : (Boolean) values[4]);
      recognition.setImgPath(values[5] == null ? "" : (String) values[5]);
      recognition.setCapImgUrl(values[6] == null ? "" : (String) values[6]);
      recognition.setSuccess(values[7] == null ? null : (Boolean) values[7]);
      recognition.setConfidence(values[8] == null ? null : (Float) values[8]);
      recognition.setDetectTime(values[9] == null ? null : (Date) values[9]);
      resultList.add(recognition);
    }
    Page<FaceRecognitionResult> result = new PageImpl<>(resultList, new ChunkRequest(param.getOffset(), param.getLimit()), total);
    return result;
  }

}
