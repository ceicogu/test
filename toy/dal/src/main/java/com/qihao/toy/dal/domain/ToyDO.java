/*
 * Copyright (c) 2002-2012 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qihao.toy.dal.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import com.qihao.shared.base.IntEnum;

@Setter @Getter
public class ToyDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	public static enum ToyStatus implements IntEnum{
		Initial(0),//初始状态
		Activated(1),//已激活
		Claimed(2);//已认领

	    private int    intValue;

	    private ToyStatus(int intValue){
	        this.intValue = intValue;
	    }


		public int intValue() {
			return intValue;
		}
	}
    private String	toySN;			//玩具唯一编码
    private String	toyName;	//玩具名称
    private String	toyMac;		//玩具MAC地址
    private ToyStatus	 status;			//状态0-初始状态/1-已激活/2-已綁定
    private Long		activatorId;  //激活者ID(toy自己的帐号ID)
    private Long		ownerId;		//绑定者
    private String	kidName;		//宝宝的名字
    private Integer	kidGender;	//宝宝性别-1待定／0-女/1-男
    private Integer	kidAge;			//宝宝年龄
    private Date		kidBirth;		//宝宝出生日期
    private Date		gmtOwned;	//绑定时间
    private Integer isDeleted;	//逻辑删除标记1-逻辑删除/0--正常
}
