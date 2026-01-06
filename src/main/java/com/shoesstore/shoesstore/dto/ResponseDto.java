package com.shoesstore.shoesstore.dto;

import java.util.Objects;

public class ResponseDto {
    private String statusCode;
    private String statusMessage;

    public ResponseDto() {
    	
    }
    
    public ResponseDto(String statusCode, String statusMessage) {
    	this.statusCode = statusCode;
    	this.statusMessage = statusMessage;
    }

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public int hashCode() {
		return Objects.hash(statusCode, statusMessage);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseDto other = (ResponseDto) obj;
		return Objects.equals(statusCode, other.statusCode) && Objects.equals(statusMessage, other.statusMessage);
	}

	@Override
	public String toString() {
		return "ResponseDto [statusCode=" + statusCode + ", statusMessage=" + statusMessage + "]";
	}
    
}
