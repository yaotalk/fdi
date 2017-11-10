package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class QueryMeetingManageParam extends PageParam {
  
  private static final long serialVersionUID = 7318749879811096019L;

  @ApiModelProperty(value = "会议名称")
  private String name;
  @ApiModelProperty(value = "会议地址")
  private String address;
  @ApiModelProperty(value = "会议开始时间")
  private Long startTime;
  @ApiModelProperty(value = "会议结束时间")
  private Long endTime;
  
}
