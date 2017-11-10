package com.minivision.fdi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QrRecMsg {
  private String meetingId;
  private String qrCode;
  private long timestamp;
}
