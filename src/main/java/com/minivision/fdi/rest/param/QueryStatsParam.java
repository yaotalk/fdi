package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class QueryStatsParam extends PageParam {
  
  private static final long serialVersionUID = 5373914950519132996L;
  
  @ApiModelProperty(value = "会议名称")
  private String meetingName = "";
  @ApiModelProperty(value = "公司名称")
  private String companyName = "";
  @ApiModelProperty(value = "姓名")
  private String name = "";

}
