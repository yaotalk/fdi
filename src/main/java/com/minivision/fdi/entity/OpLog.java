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

/**
 * 操作日志信息实体类
 * @author hughzhao
 * @2017年5月22日
 */
@Entity
@Setter
@Getter
@ToString(callSuper = true)
public class OpLog extends IdEntity {

  private static final long serialVersionUID = 4758595032806708490L;

  @Column(nullable = false)
  private String username;

  private String ip;
  private String module;
  @Column(nullable = false)
  private String operation;
  
  @Column(length = 1000)
  private String request;
  @Column(length = 1000)
  private String response;

  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date opTime;

}
