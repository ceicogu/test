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

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserDO  extends PageDO {

	private static final long serialVersionUID = -7395134204375628403L;

	private String   loginName;	//登录帐号
    private String   password;	//登录密码
    private Integer type;				//帐号类型  0-手机注册/1-Toy注册
    private Integer status;			//帐号状态：0-正常/1-冻结
    private String 	nickName;	//昵称
    private String	mobile;			//手机号
    private String	email;			//邮箱
    private String	photo;			//头像
    private Integer comeFrom;	//来源:0-扫码注册/1-邀请注册
    private String	comeSN;		//ToySN或注册邀请码
    private Long		invitorId;		//邀请人ID
    private Integer isDeleted;	//逻辑删除标记1-逻辑删除/0--正常
    private String	miRegId;		//miPushRegId
    
    //查询专用
    private List<Long>  userIds;
}
