/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.domain.enums;

import com.qihao.shared.base.DescedEnum;


public enum CommandTypeEnum implements DescedEnum{
	CONTENT_VIEW(0,"显示指令"),
	RESOURCE_DOWNLOAD(1,"下载指令"),
	HELPER_CONTORL(2,"助手计划控制指令");
    
    private int    num;
    private String desc;
    
    private CommandTypeEnum(int num, String desc){
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
