package com.qihao.shared.base.api.exception;

public class IllegalParamException extends Exception {

	private static final long serialVersionUID = -3293621015427724341L;
	/**
	   * Constructor for InvalidParamException
	   */
	  public IllegalParamException() {
	    super();
	  }
	  /**
	   * Constructor for InvalidParamException
	   */
	  public IllegalParamException(String msg) {
	    super(msg);
	  }
}
