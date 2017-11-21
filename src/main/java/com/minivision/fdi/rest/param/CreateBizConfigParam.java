package com.minivision.fdi.rest.param;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateBizConfigParam extends RestParam {

  private static final long serialVersionUID = 6510350471541496263L;
  
  @ApiModelProperty(value = "配置关联实体ID，如会议等")
  private String meetingToken;
  @ApiModelProperty(value = "设备编号")
  private String deviceSn;
  
  @NotBlank(message = "交互文本不能为空")
  @ApiModelProperty(value = "交互文本")
  private String text;
  @ApiModelProperty(value = "交互图片")
  private MultipartFile img;
  @ApiModelProperty(value = "交互音频")
  private MultipartFile audio;
  
  @ApiModelProperty(value = "识别失败重试次数，默认为3次")
  private Integer detectTries;
  
  @ApiModelProperty(value = "成功阀值")
  private Float successThreshold;
  /*@ApiModelProperty(value = "失败阀值")
  private Float failureThreshold;*/
  
  @ApiModelProperty(value = "是否开启1:1")
  private Boolean one2oneOn;
  @ApiModelProperty(value = "是否开启1:N")
  private Boolean one2NOn;
  @ApiModelProperty(value = "是否开启静默活体")
  private Boolean livebodyOn;
  
  @Override
  public String toString() {
    return "CreateBizConfigParam [meetingToken=" + meetingToken + ", deviceSn=" + deviceSn + ", text=" + text
        + ", img=" + img.getOriginalFilename() + ", audio=" + audio.getOriginalFilename()
        + ", successThreshold=" + successThreshold
        + ", one2oneOn=" + one2oneOn + ", one2NOn="
        + one2NOn + ", livebodyOn=" + livebodyOn + "]";
  }

}
