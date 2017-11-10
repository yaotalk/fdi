package com.minivision.fdi.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.minivision.fdi.entity.MeetingManage;
import com.minivision.fdi.rest.param.CreateMeetingManageParam;
import com.minivision.fdi.rest.param.QueryMeetingManageParam;
import com.minivision.fdi.rest.param.UpdateMeetingManageParam;

public interface MeetingManageService {
  
  MeetingManage createMeeting(CreateMeetingManageParam param);
  List<MeetingManage> createBatch(Iterable<MeetingManage> meetings);
  MeetingManage updateMeeting(UpdateMeetingManageParam param);
  void deleteMeeting(Long id);
  void deleteBatch(List<Long> ids);
  Page<MeetingManage> findByPage(QueryMeetingManageParam param);

}
