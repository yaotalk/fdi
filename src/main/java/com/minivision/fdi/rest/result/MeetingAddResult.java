package com.minivision.fdi.rest.result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingAddResult {

    private String meetingToken;

    public MeetingAddResult(String meetingToken) {
         this.meetingToken = meetingToken;
    }
}
