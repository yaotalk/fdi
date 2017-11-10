package com.minivision.fdi.rest.param;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FaceParam extends PageParam {

    private static final long serialVersionUID = -4633815462078667435L;

    private String name;

//    private long startTime;

 //    private long endTime;

    private String phoneNumber;

    private String companyName;

    private String meetName;

    @Override public String toString() {
        return "FaceParam{" + "name='" + name + '\'' + ", phoneNumber='" + phoneNumber + '\''
            + ", companyName='" + companyName + '\'' + ", meetName='" + meetName + '\'' + '}';
    }
}
