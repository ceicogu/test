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

package com.qihao.toy.web.api.module.screen.message;

import lombok.extern.slf4j.Slf4j;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Context;

import com.alibaba.fastjson.JSON;

import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.StationLetterService;

import com.qihao.toy.web.base.BaseApiScreenAction;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
public class SendMessage extends BaseApiScreenAction {
    @Autowired
    private StationLetterService stationLetterService;    
	/**
	 * 发送消息
	 * @param requestParams
	 * @param context
	 * @throws Exception
	 */
	public void execute(ParameterParser requestParams, Context context)
			throws Exception {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Long> result = new DataResult<Long>();

    	if(StringUtils.isBlank(requestParams.getString("acceptorId"))) {
    		log.debug("请指定接收者");
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定接收人！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	
    	int		type					=	requestParams.getInt("type",0);
    	int     acceptorType 	= requestParams.getInt("acceptorType",0);
    	long acceptorId		=	requestParams.getLong("acceptorId");
    	String content			= requestParams.getString("content");
    	String url						= requestParams.getString("url");
    	result = stationLetterService.createLetter(currentUser.getId(), acceptorType, acceptorId, type,content, url);
    	
        response.getWriter().println(JSON.toJSONString(result));
        return;        	
	}
}
