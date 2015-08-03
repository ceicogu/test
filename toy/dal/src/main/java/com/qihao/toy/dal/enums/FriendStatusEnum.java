/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.toy.dal.enums;

import com.qihao.shared.base.DescedEnum;

/**
 * 类AuditStatus.java的实现描述：TODO 类实现描述
 * 
 * @author muchen.lh 2014年6月18日 下午3:47:43
 */
public enum FriendStatusEnum implements DescedEnum{
	Inviting(0,"邀请中"),
	IsFriend(1,"已关注"),
	Cancel(2,"取消关注");
    
    private int    num;
    private String desc;
    
    private FriendStatusEnum(int num, String desc){
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
