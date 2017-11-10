package com.minivision.fdi.event;

import org.springframework.context.ApplicationEvent;

import com.minivision.fdi.entity.Meeting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingRemoveEvent extends ApplicationEvent {

  private static final long serialVersionUID = -8321462956052818832L;
  
  private Meeting meeting;

  public MeetingRemoveEvent(Object source, Meeting meeting) {
    super(source);
    this.meeting = meeting;
  }

}
