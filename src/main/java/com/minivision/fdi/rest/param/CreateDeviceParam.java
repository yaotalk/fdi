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
public class CreateDeviceParam extends RestParam {
  
  private static final long serialVersionUID = 1864627527569932988L;
  
  @ApiModelProperty(value = "设备关联实体ID，如会议等")
  private String meetingToken;

  @NotBlank(message = "设备编号不能为空")
  @ApiModelProperty(value = "设备编号", required = true)
  private String sn;
  @NotBlank(message = "设备名称不能为空")
  @ApiModelProperty(value = "设备名称", required = true)
  private String name;
  @NotBlank(message = "设备类型不能为空")
  @ApiModelProperty(value = "设备类型", required = true)
  private String model;

  @ApiModelProperty(value = "上级设备编号")
  private String parentSn;
  @ApiModelProperty(value = "设备位置")
  private String location;
  @ApiModelProperty(value = "设备联系人")
  private String contact;
  @ApiModelProperty(value = "设备IP")
  private String ip;
  @ApiModelProperty(value = "设备端口")
  private Short port;
  @ApiModelProperty(value = "设备访问地址")
  private String access;
  @ApiModelProperty(value = "设备功能，如签到、考勤、门禁等")
  private String function;
  
}
