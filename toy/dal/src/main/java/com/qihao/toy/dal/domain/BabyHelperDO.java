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

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class BabyHelperDO  extends PageDO {

	private static final long serialVersionUID = 1L;

    private Long		toyUserId;	//Toy注册帐号ID
    private String	actTime;		//时间
    private String	repeats;		//重复
    private String	tags;				//标签
    private String	url;  				//播放内容URL
    private Long		operatorId;	//设定者ID
}
