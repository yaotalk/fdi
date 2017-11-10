package com.minivision.fdi.rest.param;

import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 操作日志查询接口入参
 * @author hughzhao
 * @2017年5月22日
 */
@Setter
@Getter
@ToString
public class OpLogParam extends PageParam {

  private static final long serialVersionUID = 7057826591075875615L;
  
  @ApiModelProperty(value = "用户", required = true)
  @NotBlank(message = "用户不能为空")
  private String username;

  @ApiModelProperty(value = "查询开始时间")
  private String startTime;
  @ApiModelProperty(value = "查询截止时间")
  private String endTime;

}
