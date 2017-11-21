package com.minivision.fdi.rest.param;

import com.minivision.fdi.domain.MeetingStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetParam extends PageParam{

    private static final long serialVersionUID = 8734350369638361396L;

    private String name;

    private String address;

    private MeetingStatus status;

    private Long startTime;

    private Long endTime;

    private Long deadLine;

    @Override public String toString() {
        return "MeetParam{" + "name='" + name + '\'' + ", address='" + address + '\'' + ", status="
            + status + ", startTime=" + startTime + ", endTime=" + endTime + '}';
    }
}
