package com.minivision.fdi.rest.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MeetUpdateParam {

    @NotNull
    private String token;

    @ApiModelProperty(value = "不填时默认原始值")
    private String name;

    @ApiModelProperty(value = "不填时默认原始值")
    private String venue;

    @ApiModelProperty(value = "不填时默认原始值")
    private String address;

    @ApiModelProperty(value = "时间戳(ms),不填时默认原始值")
    private Long startTime;

    @ApiModelProperty(value = "时间戳(ms),不填时默认原始值")
    private Long endTime;

    @ApiModelProperty(value = "时间戳(ms),不填时默认原始值")
    private Long deadline;

    @Override public String toString() {
        return "MeetUpdateParam{" + "token='" + token + '\'' + ", name='" + name + '\''
            + ", venue='" + venue + '\'' + ", address='" + address + '\'' + ", startTime="
            + startTime + ", endTime=" + endTime + ", deadline=" + deadline + '}';
    }
}
