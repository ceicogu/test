package com.qihao.shared.base;

import java.io.Serializable;

public class SimpleResult implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean success;

	private Integer errorCode;

	private String message;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
