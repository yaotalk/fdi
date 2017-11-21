package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class QueryDeviceParam extends PageParam {
  
  private static final long serialVersionUID = -24474430323953545L;

  @ApiModelProperty(value = "设备编号")
  private String sn;
  @ApiModelProperty(value = "设备名称")
  private String name;
  @ApiModelProperty(value = "设备类型")
  private String model;
  
  @ApiModelProperty(value = "设备是否在线")
  private Boolean online;
  @ApiModelProperty(value = "设备是否激活")
  private Boolean activated;
  
  @ApiModelProperty(value = "设备关联实体ID，如会议等")
  private String meetingToken;
  @ApiModelProperty(value = "上级设备编号")
  private String parentSn;
  @ApiModelProperty(value = "设备位置")
  private String location;
  @ApiModelProperty(value = "设备联系人")
  private String contact;
  
}
