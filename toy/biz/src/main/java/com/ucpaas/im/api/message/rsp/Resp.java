package com.ucpaas.im.api.message.rsp;

import java.io.Serializable;

/**
 * 响应entity
 * 
 */

public class Resp implements Serializable {
	private static final long serialVersionUID = -8989474370130790923L;
	private String respCode = "106900";
	
	

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

}
