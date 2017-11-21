package com.minivision.fdi.rest.param;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateBizConfigParam extends RestParam {

  private static final long serialVersionUID = 5395936757079953615L;
  
  @NotNull(message = "配置ID不能为空")
  @Min(value = 1, message = "非法ID")
  @ApiModelProperty(value = "配置ID", required = true)
  private Long configId;
  
  @ApiModelProperty(value = "交互文本")
  private String text;
  @ApiModelProperty(value = "交互图片")
  private MultipartFile img;
  @ApiModelProperty(value = "交互音频")
  private MultipartFile audio;
  
  @ApiModelProperty(value = "识别失败重试次数")
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
    return "UpdateBizConfigParam [configId=" + configId + ", text=" + text
        + ", img=" + img.getOriginalFilename() + ", audio=" + audio.getOriginalFilename()
        + ", successThreshold=" + successThreshold
        + ", one2oneOn=" + one2oneOn + ", one2NOn=" + one2NOn + ", livebodyOn=" + livebodyOn + "]";
  }

}
