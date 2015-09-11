/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.domain.enums;

import com.qihao.shared.base.DescedEnum;


public enum VoiceCmdTypeEnum implements DescedEnum{
	PLAY(0,"播放指令"),
	CALL(1," 呼叫指令"),
	CONTROL(2,"控制指令"),
	ANSWER(3,"应答指令");
    
    private int    num;
    private String desc;
    
    private VoiceCmdTypeEnum(int num, String desc){
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
