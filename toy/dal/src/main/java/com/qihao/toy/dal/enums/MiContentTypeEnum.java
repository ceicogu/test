/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.enums;

import com.qihao.shared.base.DescedEnum;



public enum MiContentTypeEnum implements DescedEnum{
	LETTER(0,"消息类型"),
	HELPER(1,"亲子助手");
	
    private int    num;
    private String desc;
    
    private MiContentTypeEnum(int num, String desc){
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
