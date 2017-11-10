package com.minivision.fdi.event;

import org.springframework.context.ApplicationEvent;

import com.minivision.fdi.entity.Face;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceAddEvent extends ApplicationEvent {

  private static final long serialVersionUID = 8594349589430380748L;
  
  private Face face;

  public FaceAddEvent(Object source, Face face) {
    super(source);
    this.face = face;
  }

}
