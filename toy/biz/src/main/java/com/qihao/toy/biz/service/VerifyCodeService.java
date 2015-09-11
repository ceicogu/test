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

package com.qihao.toy.biz.service;

import com.qihao.toy.dal.domain.VerifyCodeDO;

/**
 * 手机短信各类验证码管理
 *
 */
public interface VerifyCodeService {

	/**
	 * 创建对应类型的验证码
	 * @param verifyCode				验证码对象
	 * @return
	 */
	public boolean createVerifyCode( Long invitorId,VerifyCodeDO.VerifyCodeType codeType, String mobile);
	public boolean createVerifyCode(Long invitorId, VerifyCodeDO.VerifyCodeType codeType, String mobile, Integer codeLength);
	/**
	 * 创建注册验证码或邀请码
	 * @param codeType      注册验证码／注册邀请码
	 * @param mobile           手机号
	 * @param codeLength   Code长度(缺省6)
	 * @param duration       失效时长（秒，缺失永久)
	 * @return
	 */
	public boolean createVerifyCode(Long invitorId, VerifyCodeDO.VerifyCodeType codeType, String mobile, Integer codeLength, Integer duration);
    /**
     * 校验手机验证码是否有效
     * @param mobile
     * @param code
     * @return
     */
    boolean checkVerifyCode(VerifyCodeDO.VerifyCodeType codeType,String mobile, String code);
    /**
     * 确认验证码已验证
     * @param userId
     * @param code
     * @return
     */
    boolean comfirmVerifyCode(VerifyCodeDO.VerifyCodeType codeType,String mobile, String code);
}
