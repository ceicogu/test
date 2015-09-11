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
/**
 * 我的交流群
 * @author luqiao
 *
 */
@Setter @Getter
public class MyGroupMemberDO  extends PageDO {

	private static final long serialVersionUID = 1L;

    private Long			groupId;		//群ID	
    private MyGroupDO.GroupType			groupType;		//群类型
    private Long			memberId;		//群成员用户ID
    private String			memberName;		//群成员群中昵称
}
