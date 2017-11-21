package com.minivision.fdi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ConfigUpdateMsg {
  
  private String text;
  private String imgUrl;
  private String audioUrl;
  
  private Integer detectTries;

  private Float successThreshold;

  private Boolean one2oneOn;
  private Boolean one2NOn;
  private Boolean livebodyOn;

}
