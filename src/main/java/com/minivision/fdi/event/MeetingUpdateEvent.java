package com.minivision.fdi.event;

import org.springframework.context.ApplicationEvent;

import com.minivision.fdi.entity.Meeting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingUpdateEvent extends ApplicationEvent {
  
  private static final long serialVersionUID = -3320649391087594341L;
  
  private Meeting meeting;

  public MeetingUpdateEvent(Object source, Meeting meeting) {
    super(source);
    this.meeting = meeting;
  }

}
