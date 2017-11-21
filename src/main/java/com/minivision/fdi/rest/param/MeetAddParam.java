package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;

@Data
public class MeetAddParam {

    @NotNull
    @ApiModelProperty(required = true)
    private String name;

    @NotNull
    @ApiModelProperty(required = true,value = "会议场所")
    private String venue;

    @NotNull
    @ApiModelProperty(required = true,value = "会议地址")
    private String address;

    @NotNull
    @ApiModelProperty(required = true,value = "时间戳(ms)")
    private Long startTime;

    @NotNull
    @ApiModelProperty(required = true,value = "时间戳(ms)")
    private Long endTime;

    @NotNull
    @ApiModelProperty(required = true,value = "签到开始时间(ms)")
    private Long signTime;

    @NotNull
    @ApiModelProperty(required = true,value = "签到截止时间(ms)")
    private Long deadline;

    @Override public String toString() {
        return "MeetAddParam{" + "name='" + name + '\'' + ", venue='" + venue + '\'' + ", address='"
            + address + '\'' + ", startTime=" + startTime + ", endTime=" + endTime + ", signTime="
            + signTime + ", deadline=" + deadline + '}';
    }
}
