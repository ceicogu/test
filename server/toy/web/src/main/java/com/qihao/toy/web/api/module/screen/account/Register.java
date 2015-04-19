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
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Context;

import com.alibaba.citrus.util.StringUtil;
import com.alibaba.fastjson.JSON;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.enums.AccountTypeEnum;
import com.qihao.toy.dal.enums.RegFromEnum;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
import com.qihao.toy.web.base.BaseApiScreenAction;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
		// 1.参数校验(0-手機註冊/1-Toy註冊
		int type = requestParams.getInt("type", 0);
		UserDO userDO = null;
		try {
			if (type == 0) {
				userDO = this.regFromMobile(requestParams);
			} else {
				userDO = this.regFromToy(requestParams);
			}
		} catch (Exception e) {
			result.setSuccess(false);
			result.setErrorCode(2001);
			result.setMessage(e.getMessage());
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		try {
			Map<String, Object> data = Maps.newTreeMap();
			String authToken = accountService.createAuthToken(userDO);
			data.put("authToken", authToken);
			data.put("uid", userDO.getId());
			data.put("nickName", userDO.getNickName());
			result.setSuccess(true);
			result.setData(data);
			response.getWriter().println(JSON.toJSONString(result));
			return;
		} catch (Exception e) {
			log.error("注册失败!userDO={},exception={}", userDO, e);
			result.setSuccess(false);
			result.setErrorCode(10002);
			result.setMessage("注册失败！原因:" + e.getMessage());
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
	}

	/**
	 * 手机注册
	 * 
	 * @param userDO
	 * @param requestParams
	 */
	private UserDO regFromMobile(ParameterParser requestParams) {
		// 1.参数校验
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("loginName")),
				"%s不能为空", "登录名");
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("pwd")), "%s不能为空",
				"登录密码");
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("code")), "%s不能为空",
				"验证码");
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("mobile")),
				"%s不能为空", "手机号码");
		// 2.校验手机验证码是否有效
		String mobile = requestParams.getString("mobile");
		String code = requestParams.getString("code");
		try {
			verifyCodeService.checkVerifyCode(
					VerifyCodeTypeEnum.Reg_VerifyCode, mobile, code);
		} catch (Exception e) {
			throw new RuntimeException("验证码无效！");
		}
		// 3.监测邀请者或Toy已注册
		Integer comeFrom = requestParams.getInt("comeFrom",
				RegFromEnum.Scan.numberValue());
		String comeSN = requestParams.getString("comeSN");
		// 获取注册来源方式
		UserDO userDO = new UserDO();
		userDO.setLoginName(requestParams.getString("loginName"));
		userDO.setPassword(requestParams.getString("pwd"));// 在biz层进行md5
		userDO.setMobile(mobile);
		userDO.setNickName(requestParams.getString("nickName"));
		userDO.setComeFrom(comeFrom);
		userDO.setComeSN(comeSN);
		userDO.setType(AccountTypeEnum.MOBILE_REG.numberValue());// 帐号类型:0-手机注册
		userDO.setMiRegId(requestParams.getString("miRegId"));
		if (StringUtils.isNumeric(requestParams.getString("invitorId"))) {
			userDO.setInvitorId(requestParams.getLong("invitorId", -1));
		}
		// 注册Toy
		userDO.setType(AccountTypeEnum.MOBILE_REG.numberValue());
		accountService.register(userDO);
		try {
			verifyCodeService.comfirmVerifyCode(
					VerifyCodeTypeEnum.Reg_VerifyCode, mobile, code);// 将验证码修改为已使用
		} catch (Exception e) {
			log.error("验证码错误");
		}
		return userDO;
	}

	/**
	 * 故事机注册
	 * 
	 * @param userDO
	 * @param requestParams
	 */
	private UserDO regFromToy(ParameterParser requestParams) {
		// 1.参数校验
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("loginName")),
				"%s不能为空", "登录名");
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("pwd")), "%s不能为空",
				"登录密码");
		Preconditions.checkState(
				!StringUtil.isBlank(requestParams.getString("comeSN")),
				"%s不能为空", "故事机二维码");

		// 2.注册:检查Toy是否已激活,若激活就不能重复注册(此时会抛出异常)
		String comeSN = requestParams.getString("comeSN");// toySN
		UserDO userDO = new UserDO();
		userDO.setType(AccountTypeEnum.TOY_REG.numberValue());// 玩具注册
		userDO.setLoginName(requestParams.getString("loginName"));// toy-mac地址
		userDO.setPassword(requestParams.getString("pwd"));// 在biz层进行md5
		userDO.setMobile(null);
		userDO.setNickName(null);
		userDO.setComeFrom(RegFromEnum.Scan.numberValue());
		userDO.setComeSN(comeSN);
		userDO.setMiRegId(requestParams.getString("miRegId"));
		if (StringUtils.isNumeric(requestParams.getString("invitorId"))) {
			userDO.setInvitorId(requestParams.getLong("invitorId", -1));
		}
		// 注册Toy
		accountService.register(userDO);

		return userDO;
	}
}
