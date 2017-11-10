package com.minivision.fdi.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.minivision.fdi.entity.Face;
import com.minivision.fdi.event.FaceAddEvent;
import com.minivision.fdi.event.FaceRemoveEvent;
import com.minivision.fdi.repository.FaceRepository;
import com.minivision.fdi.repository.MeetingManageRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FaceOperationListener {

  @Autowired
  private MeetingManageRepository meetingManageRepo;
  @Autowired
  private FaceRepository faceRepo;
  
  @EventListener
  public void faceAdd(FaceAddEvent event) {
      Face face = event.getFace();
      log.info("Listening Face Persist : " + face.getName());
      meetingManageRepo.incrementEnrollment(face.getMeeting().getToken());
  }
  
  @EventListener
  public void faceRemove(FaceRemoveEvent event) {
      log.info("Listening Face Remove : " + event.getRemoved() + " removed");
      meetingManageRepo.updateEnrollment(faceRepo.countByMeetingToken(event.getMeetingToken()), event.getMeetingToken());
  }
  
}
