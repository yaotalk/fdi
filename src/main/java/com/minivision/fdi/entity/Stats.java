package com.minivision.fdi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minivision.ai.domain.IdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString(callSuper = true)
public class Stats extends IdEntity {

  private static final long serialVersionUID = -4671375417855157591L;

  @Column(nullable = false)
  private String meetingToken;
  @Column(nullable = false)
  private String deviceSn;
  @Column(nullable = false)
  private String faceId;
  @Column(nullable = false)
  private Boolean success;
  //1:进，2:出
  @Column(nullable = false)
  private Byte type;
  private String capImgUrl;
  private Float confidence;
  
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date detectTime;
  
}
