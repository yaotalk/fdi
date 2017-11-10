package com.minivision.fdi.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceRemoveEvent extends ApplicationEvent {

  private static final long serialVersionUID = 6837725846981902986L;
  
  private String meetingToken;
  private int removed;

  public FaceRemoveEvent(Object source, String meetingToken, int removed) {
    super(source);
    this.meetingToken = meetingToken;
    this.removed = removed;
  }

}
