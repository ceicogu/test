/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.enums;

/**
 * 类AuditStatus.java的实现描述：TODO 类实现描述
 * 
 * @author muchen.lh 2014年6月18日 下午3:47:43
 */
public enum ToyStatusEnum implements DescedEnum{
	Initial(0,"初始状态"),
    Activated(1,"已激活"),
    Claimed(2,"已認領"),;
    
    private int    num;
    private String desc;
    
    private ToyStatusEnum(int num, String desc){
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
