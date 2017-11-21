package com.minivision.fdi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minivision.ai.domain.IdEntity;
import com.minivision.fdi.listener.BizConfigOperationListener;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames={"meeting_token", "device_sn"})
})
@Setter
@Getter
@ToString(callSuper = true)
@EntityListeners({AuditingEntityListener.class, BizConfigOperationListener.class})
public class BizConfig extends IdEntity {

  @Column(name = "meeting_token")
  private String meetingToken;
  @Column(name = "device_sn")
  private String deviceSn;

  @Column(nullable = false, length = 1000)
  private String text;
  private String imgUrl;
  private String audioUrl;
  
  private Integer detectTries = 3;

  private Float successThreshold;

  private Boolean one2oneOn = false;
  private Boolean one2NOn = false;
  private Boolean livebodyOn = false;

  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(nullable = false)
  private Date createTime;
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  @Column(nullable = false)
  private Date updateTime;
  
  @CreatedBy
  private String creator;
  @LastModifiedBy
  private String modifier;

  private static final long serialVersionUID = 1544113281840741871L;
  
}
