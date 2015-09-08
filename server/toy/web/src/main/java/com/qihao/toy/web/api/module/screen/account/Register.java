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

package com.qihao.toy.web.api.module.screen.account;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Context;
import com.alibaba.fastjson.JSON;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
import com.qihao.toy.web.base.BaseApiScreenAction;

@Slf4j
public class Register extends BaseApiScreenAction {
	@Autowired
	private AccountService accountService;
	@Autowired
	private ToyService toyService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private VerifyCodeService verifyCodeService;
	@Autowired
	private StationLetterService stationLetterService;
	@Autowired
	private MessageChannelService messageChannelService;

	public void execute(ParameterParser requestParams, Context context)
			throws Exception {
		// if(!"POST".equals(request.getMethod())){
		// throw new InvalidParamException("token error");
		// }
		// 参数校验
		DataResult<Map<String, Object>> result = new DataResult<Map<String, Object>>();
		UserDO userDO = new UserDO();
		userDO.setLoginName(requestParams.getString("loginName"));
		userDO.setPassword(requestParams.getString("pwd"));// 在biz层进行md5
		userDO.setMobile(requestParams.getString("mobile"));
		userDO.setNickName(requestParams.getString("nickName"));
		userDO.setComeFrom(requestParams.getInt("comeFrom",UserDO.AccoutChannel.Self.intValue()));
		userDO.setComeSN(requestParams.getString("comeSN"));
		userDO.setType(requestParams.getInt("type",UserDO.AccountType.Mobile.intValue()));
		userDO.setMiRegId(requestParams.getString("miRegId"));
		if (StringUtils.isNumeric(requestParams.getString("invitorId"))) {
			userDO.setInvitorId(requestParams.getLong("invitorId"));
		}
		
		// 1.参数校验(0-手機註冊/1-Toy註冊
		String code = requestParams.getString("code");
		if(userDO.getType() == UserDO.AccountType.Mobile.intValue()){
			if(!StringUtils.isNumeric(code)) {
				result.setSuccess(false);
				result.setErrorCode(2001);
				result.setMessage("请填写验证码!");
				response.getWriter().println(JSON.toJSONString(result));
				return;				
			}
			if(null != userDO.getMobile()){
				result.setSuccess(false);
				result.setErrorCode(2001);
				result.setMessage("手机号码不能为空!");
				response.getWriter().println(JSON.toJSONString(result));
				return;				
			}
			try {
				verifyCodeService.checkVerifyCode(
						VerifyCodeTypeEnum.Reg_VerifyCode, userDO.getMobile(), code);
			} catch (Exception e) {
				result.setSuccess(false);
				result.setErrorCode(2001);
				result.setMessage("验证码无效!");
				response.getWriter().println(JSON.toJSONString(result));
				return;	
			}
		}
		
		try {
			// 3.注册
			accountService.register(userDO);
			
			if(userDO.getType() == UserDO.AccountType.Mobile.intValue()){
				try {
					verifyCodeService.comfirmVerifyCode(
							VerifyCodeTypeEnum.Reg_VerifyCode, userDO.getMobile(), code);// 将验证码修改为已使用
				} catch (Exception e) {
					log.error("验证码错误");
				}
			}
			Map<String, Object> data = super.userDO2Map(userDO);		
			String authToken = accountService.createAuthToken(userDO);
			data.put("authToken", authToken);

			result.setSuccess(true);
			result.setData(data);
			response.getWriter().println(JSON.toJSONString(result));
			return;
		} catch (Exception e) {
			log.error("注册失败!userDO={},exception={}", userDO, e);
			result.setSuccess(false);
			result.setErrorCode(2001);
			result.setMessage("注册失败！原因:" +e.getMessage());
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
	}

}
