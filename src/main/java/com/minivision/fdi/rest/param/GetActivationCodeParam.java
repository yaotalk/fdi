package com.minivision.fdi.rest.param;

import org.hibernate.validator.constraints.NotBlank;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class GetActivationCodeParam extends RestParam {
  
  private static final long serialVersionUID = -791844805655998611L;
  
  @NotBlank(message = "设备编号不能为空")
  @ApiModelProperty(value = "设备编号", required = true)
  private String sn;
  
  @NotBlank(message = "设备类型不能为空")
  @ApiModelProperty(value = "设备类型", required = true)
  private String model;

}
