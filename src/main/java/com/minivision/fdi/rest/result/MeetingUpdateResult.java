package com.minivision.fdi.rest.result;

import lombok.Data;

@Data
public class MeetingUpdateResult {

    private String meetTingToken;

    public MeetingUpdateResult() {
    }

    public MeetingUpdateResult(String meetTingToken) {
        this.meetTingToken = meetTingToken;
    }
}
