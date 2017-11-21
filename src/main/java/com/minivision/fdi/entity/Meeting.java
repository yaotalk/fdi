package com.minivision.fdi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minivision.fdi.domain.MeetingStatus;
import com.minivision.fdi.listener.MeetingOperationListener;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@EntityListeners({AuditingEntityListener.class, MeetingOperationListener.class})
public class Meeting {

  @Id
  @NotEmpty(message = "token is required.")
  private String token;

  @Column(name = "meeting_name", unique = true, nullable = false)
  @NotEmpty(message = "meeting_name is required.")
  private String name;

  @CreatedDate
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date updateTime;

  private String venue;

  @NotBlank
  private String address;

  @NotNull
  private Long startTime;

  @NotNull
  private Long endTime;

  @NotNull
  private Long deadline;

  @NotNull
  private Long signTime;

  @Transient
  private int faceCount;

  @Transient
  private MeetingStatus status;

  @SuppressWarnings("deprecation")
  @JsonIgnore
  @ForeignKey(name = "none")
  @OneToMany(cascade = CascadeType.REMOVE,fetch = FetchType.LAZY,mappedBy="meeting")
  private Set<Face> faces;
  
  @CreatedBy
  private String creator;
  @LastModifiedBy
  private String modifier;

  public Meeting() {
  }

  public Meeting(String token) {
    this.token = token;
  }

  @Override public String toString() {
    return "Meeting{" + "token='" + token + '\'' + ", name='" + name + '\'' + ", updateTime="
        + updateTime + ", venue='" + venue + '\'' + ", address='" + address + '\'' + ", startTime="
        + startTime + ", endTime=" + endTime + ", deadline=" + deadline + ", signTime=" + signTime
        + ", faceCount=" + faceCount + ", status=" + status + ", faces=" + faces + ", creator='"
        + creator + '\'' + ", modifier='" + modifier + '\'' + '}';
  }
}
