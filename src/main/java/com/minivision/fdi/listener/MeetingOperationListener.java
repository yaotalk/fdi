package com.minivision.fdi.listener;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.minivision.fdi.domain.MeetingStatus;
import com.minivision.fdi.entity.Device;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.entity.MeetingManage;
import com.minivision.fdi.event.MeetingAddEvent;
import com.minivision.fdi.event.MeetingRemoveEvent;
import com.minivision.fdi.event.MeetingUpdateEvent;
import com.minivision.fdi.mqtt.handler.SignPadDeviceHandler;
import com.minivision.fdi.repository.DeviceRepository;
import com.minivision.fdi.repository.MeetingManageRepository;
import com.minivision.fdi.rest.param.MeetParam;
import com.minivision.fdi.rest.result.common.PageResult;
import com.minivision.fdi.service.MeetService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MeetingOperationListener {
  
  @Autowired
  private MeetingManageRepository meetingManageRepo;
  
  @Autowired
  private MeetService meetService;
  
  @Autowired
  private DeviceRepository deviceRepo;
  
  @Autowired
  private SignPadDeviceHandler padHandler;

  private void refreshFaceSet2Device(Meeting meeting) {
    MeetParam meetParam = new MeetParam();
    meetParam.setStatus(MeetingStatus.NOT_STARTED);
    try {
      PageResult<Meeting> meetingList = meetService.findByPlat(meetParam);
      List<Device> devices = deviceRepo.findByMeetingToken(meeting.getToken());
      if (!CollectionUtils.isEmpty(devices)) {
        for (Device device : devices) {
          padHandler.refreshFaceSet(device.getSn(), meetingList);
        }
      }
    } catch (Throwable e) {
      log.error("向设备推送新的会议列表发生异常", e);
    }
  }

  @EventListener
  public void meetingPersist(MeetingAddEvent event) {
      Meeting meeting = event.getMeeting();
      log.info("Listening Meeting Persist : " + meeting.getName());
      MeetingManage manage = new MeetingManage();
      manage.setToken(meeting.getToken());
      manage.setName(meeting.getName());
      manage.setAddress(meeting.getAddress());
      manage.setStartTime(new Date(meeting.getStartTime()));
      manage.setEndTime(new Date(meeting.getEndTime()));
      manage.setSignEndTime(new Date(meeting.getDeadline()));
      manage.setCreator(meeting.getCreator());
      manage.setModifier(meeting.getModifier());
      meetingManageRepo.saveAndFlush(manage);
      //向设备推送新的会议列表
      refreshFaceSet2Device(meeting);
  }
  
  @EventListener
  public void meetingUpdate(MeetingUpdateEvent event) {
      Meeting meeting = event.getMeeting();
      log.info("Listening Meeting Update : " + meeting.getName());
      MeetingManage manage = meetingManageRepo.findByToken(meeting.getToken());
      manage.setName(meeting.getName());
      manage.setAddress(meeting.getAddress());
      manage.setStartTime(new Date(meeting.getStartTime()));
      manage.setEndTime(new Date(meeting.getEndTime()));
      manage.setSignEndTime(new Date(meeting.getDeadline()));
      manage.setModifier(meeting.getModifier());
      meetingManageRepo.saveAndFlush(manage);
      //向设备推送新的会议列表
      refreshFaceSet2Device(meeting);
  }
  
  @EventListener
  public void meetingRemove(MeetingRemoveEvent event) {
      Meeting meeting = event.getMeeting();
      log.info("Listening Meeting Remove : " + meeting.getName());
      meetingManageRepo.deleteByToken(meeting.getToken());
      //向设备推送新的会议列表
      refreshFaceSet2Device(meeting);
  }
  
}
