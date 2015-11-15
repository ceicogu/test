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

import java.util.List;

import com.qihao.shared.base.IntEnum;

import lombok.Getter;
import lombok.Setter;
/**
 * 我的交流群
 * @author luqiao
 *
 */
@Setter @Getter
public class MyGroupDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	public static enum GroupType implements IntEnum{
		Family(1),//我的家庭群
		General(9);//普通用户群

	    private int    intValue;

	    private GroupType(int intValue){
	        this.intValue = intValue;
	    }


		public int intValue() {
			return intValue;
		}
	}
    private Long		myId;			
    private String		groupName;	//群名称
    private GroupType		groupType;		//群类型：1-家庭群／9-普通用户群
    
    //查询用
    private List<Long>  groupIds;
}
