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

@Setter @Getter
public class VerifyCodeDO  extends PageDO {

	private static final long serialVersionUID =1L;

    private String	mobile;			//手机号
    private String	code;				//验证码
    private Integer type;				//验证码类型：0-注册验证码/1-注册邀请码
    private Integer status;			//帐号状态：0-待验证/1-已验证/2-已失效
    private Long		invitorId;				//注册邀请人
    private Date gmtInvited;				//邀请时间
    private Integer duration;			//失效时长
}
