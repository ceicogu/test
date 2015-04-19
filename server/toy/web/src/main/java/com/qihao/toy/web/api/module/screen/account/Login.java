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

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Context;

import com.alibaba.fastjson.JSON;

import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.web.base.BaseApiScreenAction;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class Login extends BaseApiScreenAction {
	@Autowired
	private AccountService accountService;
	/**
	 * 登录
	 * @param requestParams
	 * @param context
	 * @throws Exception
	 */
	public void execute(ParameterParser requestParams, Context context)
			throws Exception {
	   	DataResult<Map<String,String>> result =new DataResult<Map<String,String>>();
			String loginName	=	requestParams.getString("loginName");
			String pwd				=	requestParams.getString("pwd");
			if(StringUtils.isBlank(loginName) || StringUtils.isBlank(pwd)) {
	    		result.setSuccess(false);
	    		result.setErrorCode(10002);
	    		result.setMessage("帐号或密码不能为空！");
	    		response.getWriter().println(JSON.toJSONString(result));
	    		return;    				
			}
	    	try{
	    		UserDO userDO = accountService.login(loginName, pwd);
	    		//生成认证token
		    	String authToken =  accountService.createAuthToken(userDO);
		    	Map<String,String>  resp = new HashMap<String,String>();
		    	resp.put("authToken", authToken);
		    	resp.put("nickName", userDO.getNickName());
		    	result.setSuccess(true);
		    	result.setData(resp);
	    	} catch(Exception e){
	    		log.error("exception={}",e);
	    		result.setSuccess(false);
	    		result.setErrorCode(1000);
	    		result.setMessage("帐号或密码错误！");
	    	}

	        response.getWriter().println(JSON.toJSONString(result));
	}
}
