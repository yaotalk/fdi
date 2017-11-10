package com.minivision.fdi.event;

import org.springframework.context.ApplicationEvent;

import com.minivision.fdi.entity.BizConfig;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BizConfigUpdateEvent extends ApplicationEvent {
  
  private static final long serialVersionUID = 1319257566777142924L;
  private BizConfig config;

  public BizConfigUpdateEvent(Object source, BizConfig config) {
    super(source);
    this.config = config;
  }

}
