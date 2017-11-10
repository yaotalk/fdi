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
public class UpdateDeviceParam extends RestParam {

  private static final long serialVersionUID = 1675281097320360880L;
  
  @NotNull(message = "设备ID不能为空")
  @Min(value = 1, message = "非法ID")
  @ApiModelProperty(value = "设备ID", required = true)
  private Long deviceId;

  @ApiModelProperty(value = "设备关联实体ID，如会议等")
  private String meetingToken;
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
