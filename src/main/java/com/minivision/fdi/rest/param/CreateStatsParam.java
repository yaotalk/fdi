package com.minivision.fdi.rest.param;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreateStatsParam extends RestParam {

  private static final long serialVersionUID = 6525887039061961765L;

  @NotBlank(message = "会议ID不能为空")
  @ApiModelProperty(value = "会议ID", required = true)
  private String meetingToken;
  @NotBlank(message = "设备编号不能为空")
  @ApiModelProperty(value = "设备编号", required = true)
  private String deviceSn;
  @NotBlank(message = "人脸ID不能为空")
  @ApiModelProperty(value = "人脸ID", required = true)
  private String faceId;
  @NotNull(message = "识别结果不能为空")
  @ApiModelProperty(value = "识别结果，true-成功，false-失败", required = true)
  private Boolean success;
  @NotNull(message = "进出类型不能为空")
  @ApiModelProperty(value = "进出类型，1-进，2-出", required = true)
  private Byte type;
  @ApiModelProperty(value = "抓拍照片Base64编码字符串")
  private String capImg;
  @ApiModelProperty(value = "识别相似度")
  private Float confidence;
  
  @NotNull(message = "识别时间不能为空")
  @ApiModelProperty(value = "识别时间", required = true)
  private Long time;
  
}
