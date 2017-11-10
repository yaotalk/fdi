package com.minivision.fdi.rest.param;

import com.minivision.ai.rest.param.RestParam;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class QueryBizConfigParam extends RestParam {

  private static final long serialVersionUID = -4542355402655735000L;
  
  @ApiModelProperty(value = "配置关联实体ID，如会议等")
  private String meetingToken;
  @ApiModelProperty(value = "设备编号")
  private String deviceSn;

}
