package com.minivision.fdi.listener;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.minivision.fdi.entity.Device;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.entity.MeetingManage;
import com.minivision.fdi.event.MeetingAddEvent;
import com.minivision.fdi.event.MeetingRemoveEvent;
import com.minivision.fdi.event.MeetingUpdateEvent;
import com.minivision.fdi.mqtt.handler.SignPadDeviceSender;
import com.minivision.fdi.repository.DeviceRepository;
import com.minivision.fdi.repository.MeetingManageRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MeetingOperationListener {
  
  @Autowired
  private MeetingManageRepository meetingManageRepo;
  
  @Autowired
  private DeviceRepository deviceRepo;
  
  @Autowired
  private SignPadDeviceSender padHandler;
  
  @Autowired
  @Qualifier("mqttSenderWorker")
  private ExecutorService mqttSenderWorker;

  private void refreshFaceSet2Device(Meeting meeting) {
    try {
      meeting.setFaceCount(meeting.getFaces().size());
      List<Device> devices = deviceRepo.findByMeetingToken(meeting.getToken());
      if (!CollectionUtils.isEmpty(devices)) {
        mqttSenderWorker.execute(() -> {
          try {
            for (Device device : devices) {
              padHandler.refreshFaceSet(device.getSn(), meeting);
            }
          } catch (Throwable e) {
            log.error("MQTT异常", e);
          }
        });
      }
    } catch (Throwable e) {
      log.error("向设备推送会议更新消息发生异常", e);
    }
  }
  
  private void unbindFaceSet(Meeting meeting) {
    try {
      List<Device> devices = deviceRepo.findByMeetingToken(meeting.getToken());
      if (!CollectionUtils.isEmpty(devices)) {
        mqttSenderWorker.execute(() -> {
          try {
            for (Device device : devices) {
              padHandler.unbindFaceSet(device.getSn());
            }
          } catch (Throwable e) {
            log.error("MQTT异常", e);
          }
        });
      }
    } catch (Throwable e) {
      log.error("向设备推送解绑会议消息发生异常", e);
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
      manage.setSignStartTime(new Date(meeting.getSignTime()));
      manage.setSignEndTime(new Date(meeting.getDeadline()));
      manage.setCreator(meeting.getCreator());
      manage.setModifier(meeting.getModifier());
      meetingManageRepo.saveAndFlush(manage);
      //向设备推送新的会议列表
      //refreshFaceSet2Device(meeting);
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
      manage.setSignStartTime(new Date(meeting.getSignTime()));
      manage.setSignEndTime(new Date(meeting.getDeadline()));
      manage.setModifier(meeting.getModifier());
      meetingManageRepo.saveAndFlush(manage);
      //向设备推送会议更新消息
      refreshFaceSet2Device(meeting);
  }
  
  @EventListener
  public void meetingRemove(MeetingRemoveEvent event) {
      Meeting meeting = event.getMeeting();
      log.info("Listening Meeting Remove : " + meeting.getName());
      meetingManageRepo.deleteByToken(meeting.getToken());
      //向设备推送解绑会议消息
      unbindFaceSet(meeting);
  }
  
}
