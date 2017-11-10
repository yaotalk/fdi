package com.minivision.fdi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FaceSetMsg {
  private String meetingToken;
  private String deviceSn;
}
