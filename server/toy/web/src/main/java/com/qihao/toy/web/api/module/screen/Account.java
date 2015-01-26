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

package com.qihao.toy.web.api.module.screen;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.turbine.dataresolver.Params;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.shared.base.utils.MD5Algorithm;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.enums.RegFromEnum;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;

/**
 * 这个例子演示了用一个screen类处理多个事件的方法。
 *
 * @author Michael Zhou
 */
@Slf4j
public class Account {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private AccountService accountService;
    @Autowired
    private VerifyCodeService verifyCodeService;    
    @Autowired
    private StationLetterService stationLetterService;    
    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private MessageChannelService messageChannelService;
    
    /** 此方法会在所有的event handler之前执行。 */
    public void beforeExecution() {
        response.setContentType("application/json");
    }

    /** 此方法会在所有的event handler之后执行。 */
    public void afterExecution() throws IOException {
        response.flushBuffer(); // 此调用并非必须，只是用来演示afterExecution方法而已
    }
    /**验证二维码是否有效*/
    public void doValidateQRCode(ParameterParser requestParams) throws IOException{
    	//签名验证
    	SimpleResult result = this.signature(requestParams);
    	if(!result.isSuccess()) {
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		
    	}
    	String toySN = requestParams.getString("code");
    	ToyDO toy = accountService.getToyBySN(toySN);
    	if(null == toy) {
    		result.setSuccess(false);
    		result.setMessage("二维码无效!");
    	}else {
    		result.setSuccess(true);
    	}
		response.getWriter().println(JSON.toJSONString(result));
		return;  
    }
    /** 手机注册 */
    public void doRegister(ParameterParser requestParams, @Params UserDO userDO) throws IOException {    	
    	//签名验证
    	SimpleResult result1 = this.signature(requestParams);
    	if(!result1.isSuccess()) {
    		response.getWriter().println(JSON.toJSONString(result1));
    		return;    		
    	}
    	//参数校验
    	DataResult<Map<String,Object>> result =new DataResult<Map<String,Object>>();

		//1.参数校验
    	if(StringUtil.isBlank(requestParams.getString("code"))) {
    		result.setSuccess(false);
    		result.setErrorCode(1000);
    		result.setMessage("验证码不能为空");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;
    	}
    	String mobile = requestParams.getString("mobile");
    	String code	=	requestParams.getString("code");
    	try{
    		verifyCodeService.checkVerifyCode(VerifyCodeTypeEnum.Reg_VerifyCode, mobile, code);
    	}catch(Exception e) {
    		result.setSuccess(false);
    		result.setErrorCode(1000);
    		result.setMessage("验证码错误！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;
    	}
    	//获取注册来源方式
    	Integer comeFrom 	=  requestParams.getInt("comeFrom", RegFromEnum.Scan.numberValue());
    	String    comeSN		=	requestParams.getString("comeSN");
    	//UserDO userDO = new UserDO();    	
    	userDO.setLoginName(requestParams.getString("loginName"));
    	userDO.setPassword(requestParams.getString("pwd"));//在biz层进行md5
    	userDO.setMobile(mobile);
    	userDO.setNickName(requestParams.getString("nickName"));
    	userDO.setComeFrom(comeFrom);
    	userDO.setComeSN(comeSN);
    	if(StringUtils.isNumeric(requestParams.getString("invitorId"))) {
    		userDO.setInvitorId(requestParams.getLong("invitorId",-1));
    	}
    	try {
    		accountService.register(userDO);
    		verifyCodeService.comfirmVerifyCode(VerifyCodeTypeEnum.Reg_VerifyCode, mobile, code);//将验证码修改为已使用
    		Map<String,Object> data = Maps.newTreeMap();
        	String authToken = accountService.createAuthToken(userDO);
        	data.put("authToken", authToken);
        	data.put("uid", userDO.getId());
        	data.put("nickName", userDO.getNickName());
        	result.setSuccess(true);
        	result.setData(data);
            response.getWriter().println(JSON.toJSONString(result));
            return;
    	}catch(Exception e){
    		log.error("注册失败!userDO={},exception={}",userDO, e);
    		result.setSuccess(false);
    		result.setErrorCode(10002);
    		result.setMessage("注册失败！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		
    	}
    }

    /** 登录 */
    public void doLogin(ParameterParser requestParams) throws IOException {
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
    /** 获取登录用户信息     */
    public void doGetUserInfo(@Param("authToken") String authToken) throws IOException {
    	DataResult<Map<String,Object>> result = new DataResult<Map<String, Object>>();
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}
        Map<String,Object> data = Maps.newTreeMap();
        data.put("userId", userDO.getId());
        data.put("nickName", userDO.getNickName());
        data.put("photo", userDO.getPhoto());
        result.setSuccess(true);
        result.setData(data);
        response.getWriter().println(JSON.toJSONString(result));
        return;    
    }
    /** 获取邮寄验证码  */
    public void doCreateVerifyCode(@Param("mobile") String mobile) throws IOException {
    	SimpleResult result = new SimpleResult();
    	//1.创建验证码，并发送
    	try{
    		verifyCodeService.createVerifyCode(VerifyCodeTypeEnum.Reg_VerifyCode, mobile);
    		result.setSuccess(true);
    	}catch(Exception e) {
    		result.setSuccess(false);
    		result.setMessage(e.getMessage());
    	}
    	response.getWriter().println(JSON.toJSONString(result));
    }
    /**
     * 命名Toy＆宝宝 
     * @param authToken
     * @throws IOException
     */
    public void doRenameToy(ParameterParser requestParams) throws IOException {
    	//签名验证
    	SimpleResult result = this.signature(requestParams);
    	if(!result.isSuccess()) {
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		
    	}
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}
    	String toySN= requestParams.getString("toySN");
    	if(StringUtil.isBlank(toySN)){
    		result.setSuccess(false);
    		result.setErrorCode(1002);
    		result.setMessage("玩具唯一码或邀请码不能为空！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		    		
    	}
    	//1.首先认领Toy
    	//给玩具和宝宝设置基本信息
    	String toyName	=	requestParams.getString("toyName");
    	Map<String,Object> kidParams = Maps.newLinkedHashMap();
    	kidParams.put("kidName", requestParams.getString("kidName"));
    	kidParams.put("kidGender", requestParams.getInt("kidGender",-1));
    	kidParams.put("kidAge", requestParams.getInt("kidAge",-1));
    	kidParams.put("kidBirth", requestParams.getDate("kidBirth", null));
    	try{
	    	accountService.renameToy(userDO.getId(), toySN, toyName, kidParams);
			result.setSuccess(true);
			response.getWriter().println(JSON.toJSONString(result));
			return;
    	}catch(Exception e){
    		result.setSuccess(false);
    		result.setErrorCode(1002);
    		result.setMessage("玩具认领失败！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		    		    		
    	}
    }
  
    /**
     * 邀请好友
     * @param requestParams
     * @throws IOException
     */
    public void doInviteFriend(ParameterParser requestParams) throws IOException {
    	SimpleResult result = new SimpleResult();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	String mobile= requestParams.getString("mobile");
    	if(StringUtil.isBlank(mobile)){
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("手机号错误！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	//1.好友邀请
    	try{
    		verifyCodeService.createVerifyCode(VerifyCodeTypeEnum.Reg_InviteCode, mobile);
    		result.setSuccess(true);
    	}catch(Exception e) {
    		result.setSuccess(false);
    		result.setMessage(e.getMessage());
    	}
    	response.getWriter().println(JSON.toJSONString(result));
    	return;    	
    }
    /**
     * 校验邀请码
     * @param requestParams
     * @throws IOException
     */
    public void doCheckInvitionCode(ParameterParser requestParams) throws IOException {
    	SimpleResult result = new SimpleResult();
    	String code= requestParams.getString("code");
    	String mobile= requestParams.getString("mobile");
    	if(StringUtil.isBlank(mobile) || StringUtil.isBlank(code)){
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("输入参数有误！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	//2.信息入库
    	VerifyCodeDO verifyCodeDO = new VerifyCodeDO();
    	verifyCodeDO.setType(1);
    	verifyCodeDO.setMobile(mobile);
    	verifyCodeDO.setCode(code);
    	verifyCodeDO.setStatus(0);
    	try{
    		verifyCodeService.checkVerifyCode(VerifyCodeTypeEnum.Reg_InviteCode, mobile, code);
    		result.setSuccess(true);
    	}catch(Exception e) {
    		result.setSuccess(false);
    		result.setMessage(e.getMessage());    		
    	}
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    /**
     * 获取我管理的Toy列表
     * @param requestParams
     * @throws IOException
     */
    public void doGetMyToys(ParameterParser requestParams) throws IOException {
    	DataResult<List<ToyDO>> result = new DataResult<List<ToyDO>>();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	List<ToyDO>  data = accountService.getMyToys(userDO.getId());
    	result.setSuccess(true);
    	result.setData(data);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    /**
     * 获取我的所有好友用户信息
     * @param requestParams
     * @throws IOException
     */
    public void doGetMyFriends(ParameterParser requestParams) throws IOException {
    	DataResult<List<UserDO>> result = new DataResult<List<UserDO>>();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	List<UserDO>  data = accountService.getMyFriends(userDO.getId());
    	result.setSuccess(true);
    	result.setData(data);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    /**
     * 获取我创建/参与的所有群
     * @param requestParams
     * @throws IOException
     */
    public void doGetMyGroups(ParameterParser requestParams) throws IOException {
    	DataResult<List<MyGroupDO>> result = new DataResult<List<MyGroupDO>>();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	//获取群类型(0-自己创建的群，1-我参加的群)
    	Integer type = requestParams.getInt("type",0);
    	List<MyGroupDO>  data = accountService.getMyGroups(type, userDO.getId());
    	result.setSuccess(true);
    	result.setData(data);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    /**
     * 获取某个群的成员信息
     * @param requestParams
     * @throws IOException
     */
    public void doGetMyGroupMembers(ParameterParser requestParams) throws IOException {
    	DataResult<List<MyGroupMemberDO>> result = new DataResult<List<MyGroupMemberDO>>();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	if(StringUtils.isBlank(requestParams.getString("groupId"))){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定群！");
            response.getWriter().println(JSON.toJSONString(result));
            return;
    	}
    	Long groupId = requestParams.getLong("groupId");
    	List<MyGroupMemberDO>  data = accountService.getMyGroupMembers(userDO.getId(), groupId);
    	result.setSuccess(true);
    	result.setData(data);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    public void doTest(@Param("authToken") String authToken, @Params StationLetterDO letter) throws IOException {
        response.getWriter().println(JSON.toJSONString(letter));
        return;    	
    }
    public void doSendLetter(ParameterParser requestParams) throws IOException {
    	DataResult<Long> result = new DataResult<Long>();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	if(StringUtils.isBlank(requestParams.getString("acceptorId"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定接收人！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	Integer	type			=	requestParams.getInt("type",0);

    	int     acceptorType = requestParams.getInt("acceptorType",0);
    	long acceptorId		=	requestParams.getLong("acceptorId");
    	String content			= requestParams.getString("content");
    	String url						= requestParams.getString("url");
    	result = stationLetterService.createLetter(userDO.getId(), acceptorType, acceptorId, type,content, url);
        response.getWriter().println(JSON.toJSONString(result));
        return;        			
    }

    
    /**
     * 获取指定消息信息
     * @param requestParams
     * @throws IOException
     */
    public void doGetLetter(ParameterParser requestParams) throws IOException {
    	DataResult<StationLetterDO> result = new DataResult<StationLetterDO>();
    	String authToken= requestParams.getString("authToken");
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}    	
    	if(StringUtils.isBlank(requestParams.getString("letterId"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定消息ID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	long letterId				= requestParams.getLong("letterId");
    	//检查获取人是否拥有查看该消息的权限
    	result = stationLetterService.getMyLetter(letterId);
    	if(result.isSuccess()) {
    		StationLetterDO letter = result.getData();
        	Integer    acceptorType = letter.getAcceptorType();
        	Long		   acceptorId	  = letter.getAcceptorId();    		
    		if(acceptorType.equals(0)) {
    			if(!acceptorId.equals(userDO.getId())) {
    	            result.setSuccess(false);
    	            result.setErrorCode(2001);
    	            result.setMessage("无权查看该消息！");
    	            response.getWriter().println(JSON.toJSONString(result));
    	            return;        				
    			}
    		}else {
    			Boolean bOK = accountService.isGroupMember(acceptorId, userDO.getId());
    			if(bOK == false){
    	            result.setSuccess(false);
    	            result.setErrorCode(2001);
    	            result.setMessage("无权查看该消息！");
    	            response.getWriter().println(JSON.toJSONString(result));
    	            return;         				
    			}
    		}
    	}
        response.getWriter().println(JSON.toJSONString(result));
        return;        			
    }
    private SimpleResult signature(ParameterParser requestParams) {
    	String[] keyList = requestParams.getKeys();
    	DataResult<Map<String,Object>> result =new DataResult<Map<String,Object>>();

    	Map<String,String> args = new TreeMap<String,String>();
        for(String key : keyList) {
        	if(key.equalsIgnoreCase("sign")) continue;
            args.put(key,  requestParams.getString(key));
        }
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            if(StringUtils.isNotBlank(entry.getValue())) {
                buf.append(entry.getKey()).append(entry.getValue());
            }
        }
        if(!StringUtils.isNotBlank(buf.toString())) {
            log.warn("参数错误！");
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("参数错误!");
    		return result;
        } 
        //2.签名比对
        String sign = requestParams.getString("sign");        
        String newSign = null;
        try {
        	newSign = MD5Algorithm.digest(buf.toString(), globalConfig.getMd5Key());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
//		if(null == newSign || !sign.equals(newSign)) {
//			    log.warn("签名失败！");
//			    result.setSuccess(false);
//			    result.setErrorCode(1000);
//			    result.setMessage("签名失败！");
//				return result;
//		}
    	
        result.setSuccess(true);
        return result;
    }
}

