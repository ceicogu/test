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
public class MyToyDO  extends PageDO {

	private static final long serialVersionUID = 1L;

    private Long		myId;			//玩具归属用户
    private String	toySN;			//玩具唯一编码
    private Long	toyUserId;		//玩具注册账号ID
}
