package com.minivision.fdi.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames={"meeting_id", "qrCode"})
})
public class Face{

  @Id
  private String id;

  private String faceToken;

  private String name;
  private Boolean vip;
  private String position;
  private String companyName;
  private String idCard;
  private String phoneNumber;

  private String reserveFir;
  private String reserveSec;
  private String reserveThi;


  private String qrCode;
  @CreatedDate
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private Date createTime;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "meeting_id",
      foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
  @NotFound(action = NotFoundAction.IGNORE)
  private Meeting meeting;

  private String imgPath;

  private boolean signIn;

  @Override public String toString() {
    return "Face{" + "id='" + id + '\'' + ", faceToken='" + faceToken + '\'' + ", name='" + name
        + '\'' + ", vip=" + vip + ", position='" + position + '\'' + ", companyName='" + companyName
        + '\'' + ", idCard='" + idCard + '\'' + ", phoneNumber='" + phoneNumber + '\''
        + ", reserveFir='" + reserveFir + '\'' + ", reserveSec='" + reserveSec + '\''
        + ", reserveThi='" + reserveThi + '\'' + ", qrCode='" + qrCode + '\'' + ", createTime="
        + createTime + ", meeting=" + (meeting==null?null:meeting.getToken()) + ", imgPath='" + imgPath + '\'' + '}';
  }
}
