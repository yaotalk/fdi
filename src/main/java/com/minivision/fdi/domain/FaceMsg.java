package com.minivision.fdi.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.minivision.fdi.entity.Face;
import lombok.Data;

@Data
public class FaceMsg {
    @JsonUnwrapped
    private Face face;
    private Float confidence;
    private Integer count;

    public FaceMsg(Face face, Float confidence,int count) {
        this.face = face;
        this.confidence = confidence;
        this.count = count;
    }

    public FaceMsg(Face face,int count) {
        this.face = face;
        this.count = count;
    }
}
