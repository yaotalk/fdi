package com.minivision.fdi.rest.result;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel
public class FaceRecognitionResult {
  
  @ApiModelProperty(value = "姓名")
  private String name;
  @ApiModelProperty(value = "是否VIP，true-是，false-否")
  private Boolean vip;
  @ApiModelProperty(value = "公司名称")
  private String companyName;
  @ApiModelProperty(value = "人脸照片")
  private String imgPath;
  @ApiModelProperty(value = "抓拍照片")
  private String capImgUrl;
  @ApiModelProperty(value = "识别结果，true-成功，false-失败")
  private Boolean success;
  @ApiModelProperty(value = "识别相似度")
  private Float confidence;
  @ApiModelProperty(value = "识别时间")
  @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
  private Date detectTime;
  @ApiModelProperty(value = "会议名称")
  private String meetingName;
  @ApiModelProperty(value = "会议地址")
  private String address;

}
