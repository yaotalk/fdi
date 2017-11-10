package com.minivision.fdi.rest.result;

import lombok.Data;

@Data
public class FaceUpdateResult {

    private int faceUpated;

    public FaceUpdateResult(int faceAdded) {
        this.faceUpated = faceAdded;
    }
}
