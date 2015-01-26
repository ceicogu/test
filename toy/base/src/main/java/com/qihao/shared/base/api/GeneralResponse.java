package com.qihao.shared.base.api;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.qihao.shared.base.api.internal.mapping.ApiField;


/**
 * API基础响应信息。
 */
public abstract class GeneralResponse implements Serializable {

    private static final long   serialVersionUID = 5014379068811962022L;

    @ApiField("Code")
    private String              code;

    @ApiField("Message")
    private String              message;

    @ApiField("HttpStatusCode")
    private Integer             httpStatusCode;

    @ApiField("RequestID")
    private String              requestId;

    private String              body;
    private Map<String, String> params;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public boolean isSuccess() {
        return (this.httpStatusCode == null || this.httpStatusCode == 200);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
