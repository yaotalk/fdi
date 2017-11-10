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
public class CreateMeetingManageParam extends RestParam {
  
  private static final long serialVersionUID = 5756093834467351828L;

  @NotBlank(message = "会议名称不能为空")
  @ApiModelProperty(value = "会议名称", required = true)
  private String name;
  @NotBlank(message = "会议地址不能为空")
  @ApiModelProperty(value = "会议地址", required = true)
  private String address;
  @NotNull(message = "参会人数不能为空")
  @ApiModelProperty(value = "参会人数", required = true)
  private Integer enrollment;
  @NotNull(message = "会议开始时间不能为空")
  @ApiModelProperty(value = "会议开始时间", required = true)
  private Long startTime;
  @NotNull(message = "会议结束时间不能为空")
  @ApiModelProperty(value = "会议结束时间", required = true)
  private Long endTime;
  @NotNull(message = "会议签到截止时间不能为空")
  @ApiModelProperty(value = "会议签到截止时间", required = true)
  private Long signEndTime;
  
}
