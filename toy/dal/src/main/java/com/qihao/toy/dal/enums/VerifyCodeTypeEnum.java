/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.enums;

public enum VerifyCodeTypeEnum implements DescedEnum{
	Reg_VerifyCode(0,"注册验证码"),
	Reg_InviteCode(1,"注册邀请码");
    
    private int    num;
    private String desc;
    
    private VerifyCodeTypeEnum(int num, String desc){
        this.num = num;
        this.desc = desc;
    }


	public int numberValue() {
		return num;
	}

	public String desc() {
		return desc;
	}
}
