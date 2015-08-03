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
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.turbine.dataresolver.Params;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.domain.MyToyDO;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
import com.qihao.toy.web.base.BaseApiScreenAction;

/**
 * 这个例子演示了用一个screen类处理多个事件的方法。
 *
 * @author Michael Zhou
 */
@Slf4j
public class Account extends BaseApiScreenAction{
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
    @Autowired
    private DefaultSolrOperator solrOperator;
    
    /**验证二维码是否有效*/
    public void doValidateQRCode(ParameterParser requestParams) throws IOException{
    	//签名验证
    	SimpleResult result = this.signature(requestParams);
    	if(!result.isSuccess()) {
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		
    	}
    	String toySN = requestParams.getString("code");
    	ToyDO toy = toyService.getItemByToySN(toySN);
    	if(null == toy) {
    		result.setSuccess(false);
    		result.setMessage("二维码无效!");
    	}else {
    		result.setSuccess(true);
    	}
		response.getWriter().println(JSON.toJSONString(result));
		return;  
    }
    /** 修改用户信息 */
    public void doModifyProfile(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	SimpleResult result = new SimpleResult();
    	
		String miRegId	=	requestParams.getString("miRegId");
		
		currentUser.setMiRegId(miRegId);
    	try{
    		 accountService.update(currentUser);
	    	result.setSuccess(true);
    	} catch(Exception e){
    		log.error("exception={}",e);
    		result.setSuccess(false);
    		result.setErrorCode(1000);
    		result.setMessage("帐号或密码错误！");
    	}

        response.getWriter().println(JSON.toJSONString(result));
    }
    /** 获取登录用户信息     */
    public void doGetUserInfo() throws IOException {    	
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Map<String,Object>> result = new DataResult<Map<String, Object>>();

        Map<String,Object> data = Maps.newTreeMap();
        data.put("userId", currentUser.getId());
        data.put("nickName", currentUser.getNickName());
        data.put("photo", currentUser.getPhoto());
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
    		verifyCodeService.createVerifyCode(null, VerifyCodeTypeEnum.Reg_VerifyCode, mobile);
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
    	Assert.notNull(currentUser, "用户未登录!");
    	//签名验证
    	SimpleResult result = this.signature(requestParams);
    	if(!result.isSuccess()) {
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
    	Map<String,String> kidParams = Maps.newLinkedHashMap();
    	kidParams.put("kidName", requestParams.getString("kidName"));
    	kidParams.put("kidGender", requestParams.getString("kidGender","-1"));
    	kidParams.put("kidAge", requestParams.getString("kidAge","-1"));
    	kidParams.put("kidBirth", requestParams.getString("kidBirth", null));
    	try{
	    	toyService.toNameToy(currentUser.getId(), toySN, toyName, kidParams);
			result.setSuccess(true);
			result.setMessage("给故事机取名成功!");
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
    	Assert.notNull(currentUser, "用户未登录!");
    	SimpleResult result = new SimpleResult();
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
    		verifyCodeService.createVerifyCode(currentUser.getId(), VerifyCodeTypeEnum.Reg_InviteCode, mobile);
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
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<Map<String,String>>> result = new DataResult<List<Map<String,String>>>();
    	ToyDO findToy = new ToyDO();
    	findToy.setOwnerId(currentUser.getId());
    	List<ToyDO> resp = toyService.getAll(findToy);    	
    	List<Map<String,String>> data = Lists.newArrayList();    	
    	for(ToyDO toy : resp) {
    		Map<String,String> item = Maps.newTreeMap();
    		item.put("toyName", toy.getToyName());
    		item.put("kidName", toy.getKidName());
    		if(null != toy.getKidGender()){
    		item.put("kidGender", toy.getKidGender().toString());
    		}
    		if(null != toy.getKidAge()){
    		item.put("kidAge", toy.getKidAge().toString());
    		}
    		item.put("ownerId", toy.getOwnerId().toString());
    		if(null != toy.getActivatorId()) {
    			item.put("activatorId", toy.getActivatorId().toString());
    		}
    		data.add(item);
    	}
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
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<Map<String,String>>> result = new DataResult<List<Map<String,String>>>();

    	List<UserDO>  resp = accountService.getMyFriends(currentUser.getId());
    	List<Map<String,String>> data = Lists.newArrayList();    	
    	for(UserDO user : resp) {
    		Map<String,String> item = Maps.newTreeMap();
    		item.put("nickName", user.getNickName());
    		item.put("userId",user.getId().toString());
    		item.put("status",user.getStatus().toString());
    		item.put("type", user.getType().toString());
    		data.add(item);
    	}
    	result.setSuccess(true);
    	result.setData(data);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    /**
     * 获得我管理toy的好友列表
     * @param requestParams
     * @throws IOException
     */
    public void doGetMyToyFriends(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<Map<String,String>>> result = new DataResult<List<Map<String,String>>>();
    	Long toyUserId= requestParams.getLong("user_id");
    	if(null == toyUserId){
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请指定所管理toy的ID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	//检查该userID是否是本人管理的故事机
    	List<Long> toyUserIds = toyService.getMyToyUserIds(currentUser.getId());  
    	if(null == toyUserIds || !toyUserIds.contains(toyUserId)){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请所管理toy的ID无效！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}
    	//获取该toy的所有好友列表
    	List<UserDO>  resp = accountService.getMyFriends(toyUserId);
    	List<Map<String,String>> data = Lists.newArrayList();    	
    	for(UserDO user : resp) {
    		Map<String,String> item = Maps.newTreeMap();
    		item.put("nickName", user.getNickName());
    		item.put("userId",user.getId().toString());
    		item.put("status",user.getStatus().toString());
    		item.put("type", user.getType().toString());
    		data.add(item);
    	}
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
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<MyGroupDO>> result = new DataResult<List<MyGroupDO>>();
	
    	//获取自己创建的群    	
    	List<MyGroupDO>  data = groupService.getMyCreatedGroups(currentUser.getId(),null);
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
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<MyGroupMemberDO>> result = new DataResult<List<MyGroupMemberDO>>();
    	if(StringUtils.isBlank(requestParams.getString("groupId"))){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定群！");
            response.getWriter().println(JSON.toJSONString(result));
            return;
    	}
    	Long groupId = requestParams.getLong("groupId");
    	List<MyGroupMemberDO>  data = groupService.getGroupMembersByGroupId(groupId);
    	result.setSuccess(true);
    	result.setData(data);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }
    public void doTest(@Param("authToken") String authToken, @Params StationLetterDO letter) throws IOException {
        response.getWriter().println(JSON.toJSONString(letter));
        return;    	
    }
    public void doSendLetter(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Long> result = new DataResult<Long>();

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
    	result = stationLetterService.createLetter(currentUser.getId(), acceptorType, acceptorId, type,content, url);
    	
        response.getWriter().println(JSON.toJSONString(result));
        return;        			
    }
    /**
     * 分页获取群消息
     * @param requestParams
     * @throws IOException
     */
    public void doGetGroupLetters(ParameterParser requestParams) throws IOException{
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<StationLetterDO>> result = new DataResult<List<StationLetterDO>>();

    	if(StringUtils.isBlank(requestParams.getString("groupId"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请自己所在的群！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	//判断自己是否在该群
    	Long groupId = requestParams.getLong("groupId");
    	boolean bOK = groupService.isGroupMember(groupId, currentUser.getId());
    	if(false == bOK){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请自己所在的群！");
            response.getWriter().println(JSON.toJSONString(result));
            return;        		
    	}
    	//分页获取群信息
    	int page = requestParams.getInt("page",1);
    	
    	result= stationLetterService.getMyLetters(1, groupId, page, 20);
    	
        response.getWriter().println(JSON.toJSONString(result));
        return;       
    }
    /**
     * 分页获取群消息
     * @param requestParams
     * @throws IOException
     */
    public void doGetO2OLetters(ParameterParser requestParams) throws IOException{
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<StationLetterDO>> result = new DataResult<List<StationLetterDO>>();

    	if(StringUtils.isBlank(requestParams.getString("o2oId"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请自己所在的群！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	//判断自己是否是好友
    	Long o2oId = requestParams.getLong("o2oId");
    	boolean bOK =accountService.isMyFriend(currentUser.getId(), o2oId);
    	if(false == bOK){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("不是好友！");
            response.getWriter().println(JSON.toJSONString(result));
            return;        		
    	}
    	//分页获取群信息
    	int page = requestParams.getInt("page",1);
    	
    	result= stationLetterService.getMyLetters(currentUser.getId(), o2oId, page,  20);
    	
        response.getWriter().println(JSON.toJSONString(result));
        return;       
    }
    /**
     * 获取指定消息信息
     * @param requestParams
     * @throws IOException
     */
    public void doGetLetter(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<StationLetterDO> result = new DataResult<StationLetterDO>();

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
    			if(!acceptorId.equals(currentUser.getId())) {
    	            result.setSuccess(false);
    	            result.setErrorCode(2001);
    	            result.setMessage("无权查看该消息！");
    	            result.setData(null);
    	            response.getWriter().println(JSON.toJSONString(result));
    	            return;        				
    			}
    		}else {
    			Boolean bOK = groupService.isGroupMember(acceptorId, currentUser.getId());
    			if(bOK == false){
    	            result.setSuccess(false);
    	            result.setErrorCode(2001);
    	            result.setMessage("无权查看该消息！");
    	            result.setData(null);
    	            response.getWriter().println(JSON.toJSONString(result));
    	            return;         				
    			}
    		}
    	}
        response.getWriter().println(JSON.toJSONString(result));
        return;        			
    }
    /**
     * 更新当前帐号的miRegId
     * @param requestParams
     * @throws IOException
     */
    public void doSaveMiRegId(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	SimpleResult result = new SimpleResult(); 	
    	String miRegId = requestParams.getString("miRegId");
    	if(StringUtils.isBlank(miRegId)) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("miRegId不能为空！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	currentUser.setMiRegId(miRegId);
    	accountService.update(currentUser);//保存miRegId
    	result.setSuccess(true);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }    

    public void doAnalysis(@Param("q") String query) throws Exception {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<String>> result  = new DataResult<List<String>>();
    	List<String> resp = solrOperator.anlysisSolrResult("account",query);
     	result.setSuccess(true);
     	result.setMessage("分词成功!");
     	result.setData(resp);
         response.getWriter().println(JSON.toJSONString(result));
         return;    	
    }
}

