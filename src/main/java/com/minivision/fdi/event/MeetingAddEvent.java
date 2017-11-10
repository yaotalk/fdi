package com.minivision.fdi.event;

import org.springframework.context.ApplicationEvent;

import com.minivision.fdi.entity.Meeting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingAddEvent extends ApplicationEvent {
  
  private static final long serialVersionUID = 8489377083113907307L;
  
  private Meeting meeting;

  public MeetingAddEvent(Object source, Meeting meeting) {
    super(source);
    this.meeting = meeting;
  }

}
