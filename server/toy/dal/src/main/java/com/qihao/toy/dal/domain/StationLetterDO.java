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

import com.qihao.shared.base.IntEnum;

import lombok.Getter;
import lombok.Setter;
/**
 * 发布消息表
 * @author luqiao
 *
 */
@Setter @Getter
public class StationLetterDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	public static enum MediaType implements IntEnum{
		TEXT(0),//文字
		SOUND(1);//语音

	    private int    intValue;

	    private MediaType(int intValue){
	        this.intValue = intValue;
	    }


		public int intValue() {
			return intValue;
		}
	}
    private Long			senderId;					//消息发送者		
    private Integer		acceptorType;		//消息接收者类型:0-单个用户/1-我的好友/2-我的交流群
    private Long			acceptorId;				//好友ID/我的交流群ID(当type=1是记得是userId)
    private MediaType		type;							//类型0-文字/1-语音
    private String		content;					//消息内容
    private String		url;								//资源存储URL
}
