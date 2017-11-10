package com.minivision.fdi.rest.param;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UpdateMeetingManageParam extends RestParam {
  
  private static final long serialVersionUID = -7261584845356525543L;

  @NotNull(message = "会议ID不能为空")
  @Min(value = 1, message = "非法ID")
  @ApiModelProperty(value = "会议ID", required = true)
  private Long meetingId;
  
  @ApiModelProperty(value = "会议地址")
  private String address;
  @ApiModelProperty(value = "参会人数")
  private Integer enrollment;
  @ApiModelProperty(value = "会议开始时间")
  private Long startTime;
  @ApiModelProperty(value = "会议结束时间")
  private Long endTime;
  @ApiModelProperty(value = "会议签到截止时间")
  private Long signEndTime;
  
}
