package com.qihao.shared.base.api;

/*
 *  API exception 
 * 
 */
public class GeneralApiException extends Exception {
    private static final long serialVersionUID = 1L;

    private String            errCode;
    private String            errMsg;

    public GeneralApiException() {
        super();
    }

    public GeneralApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralApiException(String message) {
        super(message);
    }

    public GeneralApiException(Throwable cause) {
        super(cause);
    }

    public GeneralApiException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}
