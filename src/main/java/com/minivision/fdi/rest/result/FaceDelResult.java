package com.minivision.fdi.rest.result;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

import com.minivision.fdi.rest.result.common.FailureDetail;

@Getter
@Setter
public class FaceDelResult {
	private int faceRemoved;
	private List<FailureDetail> failureDetail;

	public FaceDelResult() {
	}

	public FaceDelResult(int faceRemoved) {
		this.faceRemoved = faceRemoved;
	}

	@Override public String toString() {
		return "FaceDelResult{" + '\'' + ", faceRemoved="
			+ faceRemoved + ", failureDetail=" + failureDetail + '}';
	}
}
