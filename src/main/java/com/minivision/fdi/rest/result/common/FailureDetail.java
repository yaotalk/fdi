package com.minivision.fdi.rest.result.common;

public class FailureDetail {
	private String faceToken;
	private String reason;

	public FailureDetail() {
	}

	public FailureDetail(String faceToken, String reason) {
		this.faceToken = faceToken;
		this.reason = reason;
	}

	public String getFaceToken() {
		return faceToken;
	}

	public void setFaceToken(String faceToken) {
		this.faceToken = faceToken;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override public String toString() {
		return "FailureDetail{" + "faceToken='" + faceToken + '\'' + ", reason='" + reason + '\'' + '}';
	}
}
