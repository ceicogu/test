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

import com.qihao.shared.base.IntEnum;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class MyFriendDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	public static enum FriendStatus implements IntEnum{
		Inviting(0),//邀请中
		IsFriend(1),//已关注
		Cancel(2);//取消关注
    
	    private int    intValue;

	    private FriendStatus(int intValue){
	        this.intValue = intValue;
	    }


		public int intValue() {
			return intValue;
		}
	}
    private Long		myId;			
    private Long		friendId;		//好友ID
    private String	relation;		//自己对好友的关系称谓
    private FriendStatus status;			//好友状态:0-邀请中/1-已关注/1-取消关注
    private String		photo;
    private Date		gmtInvited;//邀请时间
    private Date		gmtConfirmed;//关注时间
}
