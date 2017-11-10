package com.minivision.fdi.mqtt.message;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Status {
  
  private float cpu;
  private float mem;
  private Date timestamp;
  
}
