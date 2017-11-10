package com.minivision.fdi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FaceRecMsg {
  private String meetingId;
  private String feature;
  private long timestamp;
}
