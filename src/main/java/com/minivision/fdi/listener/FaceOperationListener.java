package com.minivision.fdi.listener;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.minivision.fdi.entity.Device;
import com.minivision.fdi.entity.Face;
import com.minivision.fdi.entity.Meeting;
import com.minivision.fdi.event.FaceAddEvent;
import com.minivision.fdi.event.FaceRemoveEvent;
import com.minivision.fdi.mqtt.handler.SignPadDeviceSender;
import com.minivision.fdi.repository.DeviceRepository;
import com.minivision.fdi.repository.FaceRepository;
import com.minivision.fdi.repository.MeetRepository;
import com.minivision.fdi.repository.MeetingManageRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FaceOperationListener {

  @Autowired
  private MeetingManageRepository meetingManageRepo;
  @Autowired
  private FaceRepository faceRepo;
  @Autowired
  private DeviceRepository deviceRepo;
  @Autowired
  private MeetRepository meetRepo;
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
  
  @EventListener
  public void faceAdd(FaceAddEvent event) {
      Face face = event.getFace();
      log.info("Listening Face Persist : " + face.getName());
      meetingManageRepo.incrementEnrollment(face.getMeeting().getToken());
      refreshFaceSet2Device(meetRepo.findOne(face.getMeeting().getToken()));
  }
  
  @EventListener
  public void faceRemove(FaceRemoveEvent event) {
      log.info("Listening Face Remove : " + event.getRemoved() + " removed");
      meetingManageRepo.updateEnrollment(faceRepo.countByMeetingToken(event.getMeetingToken()), event.getMeetingToken());
      refreshFaceSet2Device(meetRepo.findOne(event.getMeetingToken()));
  }
  
}
