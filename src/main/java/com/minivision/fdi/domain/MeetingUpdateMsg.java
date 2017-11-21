package com.minivision.fdi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MeetingUpdateMsg {
  
  private String token;
  private String name;
  private long deadline;

}
