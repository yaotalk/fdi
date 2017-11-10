package com.minivision.fdi.rest.result;

import lombok.Data;

@Data
public class FaceAddResult {

    private String id;

    private int faceAdded;

    public FaceAddResult(String id, int faceAdded) {
        this.id = id;
        this.faceAdded = faceAdded;
    }
}
