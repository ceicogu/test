package com.qihao.shared.base.api.response;

import com.qihao.shared.base.api.GeneralResponse;
import com.qihao.shared.base.api.internal.mapping.ApiField;

public class RegisterResponse extends GeneralResponse {

	private static final long serialVersionUID = 8616934422917100909L;

    @ApiField("userID")
    private Long           userId;
    
    @ApiField("nickName")
    private String           nickName;
    @ApiField("authToken")
    private String           authToken;
    
}
