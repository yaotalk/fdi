package com.minivision.fdi.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minivision.ai.domain.IdEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Setter
@Getter
@ToString(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class Device extends IdEntity {

  @Column(unique = true, nullable = false)
  private String sn;
  @Column(unique = true, nullable = false)
  private String name;
  @Column(nullable = false)
  private String model;
  
  private Boolean online = false;
  private Boolean activated = false;
  
  @Temporal(TemporalType.TIMESTAMP)
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date activateTime;
  
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "meeting_token",
      foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
  @NotFound(action = NotFoundAction.IGNORE)
  private Meeting meeting;
  //private String meetingToken;
  private String parentSn;
  private String location;
  private String contact;
  private String ip;
  private Short port;
  private String access;
  private String function;
  
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
  
  private static final long serialVersionUID = -4369437911753227102L;
  
}
