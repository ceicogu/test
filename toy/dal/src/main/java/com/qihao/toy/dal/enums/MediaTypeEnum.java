/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.enums;

/**
 * 站内信消息类型
 */
public enum MediaTypeEnum implements DescedEnum{
	TEXT(0,"文字"),
	SOUND(1,"语音"),
	PHOTO(2,"图片"),
	VIEDO(3,"视屏");
    
    private int    num;
    private String desc;
    
    private MediaTypeEnum(int num, String desc){
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
