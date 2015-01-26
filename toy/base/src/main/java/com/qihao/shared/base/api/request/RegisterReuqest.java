package com.qihao.shared.base.api.request;

import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import lombok.Getter;
import lombok.Setter;

import com.qihao.shared.base.api.GeneralRequest;
import com.qihao.shared.base.api.internal.util.GeneralHashMap;
import com.qihao.shared.base.api.response.RegisterResponse;
@Setter
@Getter
public class RegisterReuqest implements GeneralRequest<RegisterResponse> {
    // add user-defined text parameters
	 private GeneralHashMap udfParams;
	 
    private String        apiVersion = "1.0";
    //请求唯一ID
    private String        requestId;
    
    private String		loginName;
    private String		nickName;
    private String		password;
    private String		toySN;
    
	public String getApiMethodName() {
		return "Register";
	}

	public Map<String, String> getTextParams() {
        GeneralHashMap txtParams = new GeneralHashMap();
        txtParams.put("requestId", this.getRequestId());
        txtParams.put("loginName", this.getLoginName());
        txtParams.put("nickName", this.getNickName());
        txtParams.put("password", this.getPassword());
        txtParams.put("toySN", this.getToySN());
        
        if (udfParams != null) {
            txtParams.putAll(this.udfParams);
        }
        return txtParams;
	}

    public void putOtherTextParam(String key, String value) {
        if (this.udfParams == null) {
            this.udfParams = new GeneralHashMap();
        }
        this.udfParams.put(key, value);
    }

	public Class<RegisterResponse> getResponseClass() {
		return RegisterResponse.class;
	}
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
