package com.minivision.fdi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.minivision.ai.util.ChunkRequest;
import com.minivision.fdi.common.BeanPropertyUtils;
import com.minivision.fdi.entity.MeetingManage;
import com.minivision.fdi.repository.MeetingManageRepository;
import com.minivision.fdi.rest.param.CreateMeetingManageParam;
import com.minivision.fdi.rest.param.QueryMeetingManageParam;
import com.minivision.fdi.rest.param.UpdateMeetingManageParam;

@Service
@Transactional(rollbackFor={Exception.class})
public class MeetingManageServiceImpl implements MeetingManageService {

  @Autowired
  private MeetingManageRepository meetingRepo;

  @Override
  public MeetingManage createMeeting(CreateMeetingManageParam param) {
    MeetingManage meeting = new MeetingManage();
    BeanUtils.copyProperties(param, meeting);
    meeting.setStartTime(new Date(param.getStartTime()));
    meeting.setEndTime(new Date(param.getEndTime()));
    meeting.setSignEndTime(new Date(param.getSignEndTime()));
    return meetingRepo.saveAndFlush(meeting);
  }
  
  @Override
  public List<MeetingManage> createBatch(Iterable<MeetingManage> meetings) {
    return meetingRepo.save(meetings);
  }

  @Override
  public MeetingManage updateMeeting(UpdateMeetingManageParam param) {
    MeetingManage existed = meetingRepo.findOne(param.getMeetingId());
    BeanPropertyUtils.copyProperties(param, existed);
    if (param.getStartTime() != null && param.getStartTime() != 0) {
      existed.setStartTime(new Date(param.getStartTime()));
    }
    if (param.getEndTime() != null && param.getEndTime() != 0) {
      existed.setEndTime(new Date(param.getEndTime()));
    }
    if (param.getSignEndTime() != null && param.getSignEndTime() != 0) {
      existed.setSignEndTime(new Date(param.getSignEndTime()));
    }
    return meetingRepo.saveAndFlush(existed);
  }

  @Override
  public void deleteMeeting(Long id) {
    meetingRepo.delete(id);
  }

  @Override
  public void deleteBatch(List<Long> ids) {
    //meetingRepo.deleteInBatch(meetingRepo.findAll(ids));
    meetingRepo.deleteByIdIn(ids);
    // ToDo
    // 通知设备更新会议列表
  }

  @Override
  public Page<MeetingManage> findByPage(QueryMeetingManageParam param) {
    return meetingRepo.findAll(new Specification<MeetingManage>() {
      @Override
      public Predicate toPredicate(Root<MeetingManage> root, CriteriaQuery<?> query,
          CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if(StringUtils.hasText(param.getName())){
          predicates.add(cb.like(root.get("name"), "%" + param.getName() + "%"));
        }
        if(StringUtils.hasText(param.getAddress())){
          predicates.add(cb.like(root.get("address"), "%" + param.getAddress() + "%"));
        }
        
        Date startDate = null;
        Date endDate = null;
        if (param.getStartTime() != null && param.getStartTime() != 0) {
          startDate = new Date(param.getStartTime());
          predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), startDate));
        }
        if (param.getEndTime() != null && param.getEndTime() != 0) {
          endDate = new Date(param.getEndTime());
          predicates.add(cb.lessThanOrEqualTo(root.get("endTime"), endDate));
        }
        
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
      }
    }, new ChunkRequest(param.getOffset(), param.getLimit(), new Sort(Sort.Direction.DESC, "updateTime")));
  }

}
