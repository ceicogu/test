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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.citrus.turbine.dataresolver.Params;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
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
import com.qihao.toy.dal.domain.MyFriendDO;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.StationLetterRecordDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.domain.MyGroupDO.GroupType;
import com.qihao.toy.dal.domain.UserDO.AccountType;
import com.qihao.toy.dal.domain.UserDO.AccoutChannel;
import com.qihao.toy.dal.persistent.StationLetterRecordMapper;
import com.qihao.toy.web.api.base.BaseApiScreenAction;

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
    private StationLetterRecordMapper stationLetterRecordMapper;  
    @Autowired
    private MessageChannelService messageChannelService;
    @Autowired
    private DefaultSolrOperator solrOperator;
    
	private static SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	
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
    /**
     * 代理注册
     * @param requestParams
     * @param context
     * @throws Exception
     */
	public void doProxyRegister(ParameterParser requestParams)
			throws IOException {
		Assert.notNull(currentUser, "用户未登录!");
		// 参数校验
		DataResult<Map<String, Object>> result = new DataResult<Map<String, Object>>();
		List<ToyDO> resp = toyService.getMyManageToys(currentUser.getId());
        if (CollectionUtils.isEmpty(resp)) {
			log.error("代理注册失败!");
			result.setSuccess(false);
			result.setErrorCode(2001);
			response.getWriter().println(JSON.toJSONString(result));
			return ;
        }	
        if(StringUtils.isBlank(requestParams.getString("mobile"))){
			result.setSuccess(false);
			result.setErrorCode(2001);
			result.setMessage("手机号码不能为空!");
			response.getWriter().println(JSON.toJSONString(result));
			return ;
        }
        if(StringUtils.isBlank(requestParams.getString("nickName"))){
			result.setSuccess(false);
			result.setErrorCode(2001);
			result.setMessage("昵称不能为空!");
			response.getWriter().println(JSON.toJSONString(result));
			return ;
        }
		String mobile = requestParams.getString("mobile");
		String nickName = requestParams.getString("nickName");
		
		UserDO userDO = new UserDO();
		userDO.setLoginName(mobile);
		userDO.setPassword(mobile);// 在biz层进行md5
		userDO.setMobile(mobile);
		userDO.setNickName(nickName);
		userDO.setComeFrom(UserDO.AccoutChannel.Proxy);
		userDO.setType(UserDO.AccountType.Mobile);
		userDO.setInvitorId(currentUser.getId());
		
		try {
			// 3.注册
			accountService.register(userDO);
			
			result.setSuccess(true);
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
    /** 修改用户信息 */
    public void doModifyProfile(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	SimpleResult result = new SimpleResult();
    	
		UserDO user = new UserDO();
		user.setId(currentUser.getId());

		if(StringUtils.isNotBlank(requestParams.getString("loginName"))){
			user.setLoginName(requestParams.getString("loginName"));
		}
		if(StringUtils.isNotBlank(requestParams.getString("nickName"))){
			user.setNickName(requestParams.getString("nickName"));
		}
		if(StringUtils.isNotBlank(requestParams.getString("email"))){
			user.setEmail(requestParams.getString("email"));
		}
		if(StringUtils.isNotBlank(requestParams.getString("photo"))){
			user.setPhoto(requestParams.getString("photo"));
		}		
		if(StringUtils.isNotBlank(requestParams.getString("deviceType"))){
			user.setDeviceType(Enum.valueOf(UserDO.DeviceType.class,requestParams.getString("deviceType")));
		}
		if(StringUtils.isNotBlank(requestParams.getString("deviceToken"))){
			user.setDeviceToken(requestParams.getString("deviceToken"));
		}
        if(StringUtils.isNotBlank(requestParams.getString("voipClientNo"))){
            user.setVoipClientNo(requestParams.getString("voipClientNo"));
        }
        if(StringUtils.isNotBlank(requestParams.getString("voipClientPwd"))){
            user.setVoipClientPwd(requestParams.getString("voipClientPwd"));
        }
		try{
    		accountService.update(user);
	    	result.setSuccess(true);
    	} catch(Exception e){
    		log.error("exception={}",e);
    		result.setSuccess(false);
    		result.setErrorCode(1000);
    		result.setMessage("帐号或密码错误！");
    	}

        response.getWriter().println(JSON.toJSONString(result));
    }
    /** 获取用户信息     */
    public void doGetUserInfo(@Param("userId") Long userId) throws IOException {    	
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Map<String,Object>> result = new DataResult<Map<String, Object>>();
    	UserDO userDO = null;
    	if(null == userId) {
    		userDO = currentUser;
    	}else {
    		//TODO 检查所查用户是否自己可见
    		userDO = accountService.getUser(userId);
    	}
        result.setSuccess(true);
        result.setData(super.userDO2Map(userDO));
        response.getWriter().println(JSON.toJSONString(result));
        return;    
    }
    /**
     * 获取我管理故事机详情信息
     * @param toySN
     * @param toyUserId
     * @throws IOException
     */
    public void doGetMyToyInfo(@Param("toySN") String toySN, @Param("toyUserId") Long toyUserId) throws IOException{
    	DataResult<ToyDO> result = new DataResult<ToyDO>();
    	if(StringUtils.isBlank(toySN)
    			&& null==toyUserId) {
    		result.setSuccess(false);
    		result.setMessage("请指定故事机账号或SN号!");
    		response.getWriter().println(JSON.toJSONString(result)); 
    		return;
    	}
    	ToyDO toy = null;
    	if(StringUtils.isBlank(toySN)){
    		toy = toyService.getItemByToySN(toySN);
    	}else {
    		ToyDO searchToy = new ToyDO();
    		searchToy.setActivatorId(toyUserId);
    		List<ToyDO> resp = toyService.getAll(searchToy);
    		if(!CollectionUtils.isEmpty(resp)){
    			toy = resp.get(0);
    		}   		
    	}
    	if(null == toy  
    			|| !accountService.isMyToy(currentUser.getId(), toy.getActivatorId())){
    		result.setSuccess(false);
    		result.setMessage("指定故事机不存在!");
    		response.getWriter().println(JSON.toJSONString(result)); 
    		return;    		
    	}
    	result.setSuccess(true);
    	result.setData(toy);
    	response.getWriter().println(JSON.toJSONString(result));   	
    }
    /** 获取邮寄验证码  */
    public void doCreateVerifyCode(@Param("mobile") String mobile) throws IOException {
    	SimpleResult result = new SimpleResult();
    	//1.创建验证码，并发送
    	try{
    		verifyCodeService.createVerifyCode(null, VerifyCodeDO.VerifyCodeType.Reg_VerifyCode, mobile);
    		result.setSuccess(true);
    	}catch(Exception e) {
    		result.setSuccess(false);
    		result.setMessage(e.getMessage());
    	}
    	response.getWriter().println(JSON.toJSONString(result));
    }
    /**
     * 命名Toy＆宝宝
     * @param requestParams
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
    	
    	Long toyUserId= requestParams.getLong("toyUserId",-1);
    	if(-1 == toyUserId 
    			||StringUtils.isBlank(requestParams.getString("toySN"))){
    		result.setSuccess(false);
    		result.setErrorCode(1002);
    		result.setMessage("玩具唯一码或邀请码不能为空！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;    		    		
    	}
    	ToyDO toy = null;
    	String toySN = null;
    	//获取故事机SN
    	if(StringUtils.isNotBlank(requestParams.getString("toySN"))){
    		toySN = requestParams.getString("toySN");
    		toy = toyService.getItemByToySN(toySN);
    	}
    	if(null == toy && -1 != toyUserId) {
    		toy = toyService.getMyManageToy(currentUser.getId(), toyUserId);
    		toySN = toy.getToySN();
    	}
     	if(null == toy){
    		result.setSuccess(false);
    		result.setErrorCode(1002);
    		result.setMessage("您无权对该故事机进行命名！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return;
    	}

		if(!toy.getStatus().equals(ToyDO.ToyStatus.Claimed)) {
    		result.setSuccess(false);
    		result.setErrorCode(1002);
    		result.setMessage("故事机已被认领！");
    		response.getWriter().println(JSON.toJSONString(result));
    		return; 
		}
		if(!toy.getOwnerId().equals(currentUser.getId())){
    		result.setSuccess(false);
    		result.setErrorCode(1002);
    		result.setMessage("您不是故事机主人!");
    		response.getWriter().println(JSON.toJSONString(result));
    		return; 
		}
    	if(StringUtils.isNotBlank(requestParams.getString("toyName"))){
    		toy.setToyName(requestParams.getString("toyName"));
    	}
    	if(StringUtils.isNotBlank(requestParams.getString("kidGender"))){
    		toy.setKidGender(Enum.valueOf(ToyDO.Gender.class, requestParams.getString("kidGender")));
    	}
    	if(StringUtils.isNotBlank(requestParams.getString("kidName"))){
    		toy.setKidName(requestParams.getString("kidName"));
    	}
    	if(StringUtils.isNotBlank(requestParams.getString("kidBirdth"))){
    		toy.setKidBirth(requestParams.getDate("kidBirth",new SimpleDateFormat("yyyy-MM-dd")));
    	}

    	try{
 	    	toyService.update(toySN, toy);
	    	if(StringUtils.isNotBlank(requestParams.getString("kidName"))){
		    	UserDO user = new UserDO();
		    	user.setId(toy.getActivatorId());
		    	user.setNickName(requestParams.getString("kidName"));
		    	accountService.update(user);
	    	}
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
    		verifyCodeService.createVerifyCode(currentUser.getId(), VerifyCodeDO.VerifyCodeType.Reg_InviteCode, mobile);
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
    	verifyCodeDO.setType(VerifyCodeDO.VerifyCodeType.Reg_InviteCode);
    	verifyCodeDO.setMobile(mobile);
    	verifyCodeDO.setCode(code);
    	verifyCodeDO.setStatus(VerifyCodeDO.VerifyCodeStatus.Initial);
    	try{
    		verifyCodeService.checkVerifyCode(VerifyCodeDO.VerifyCodeType.Reg_InviteCode, mobile, code);
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
    	DataResult<List<Map<String,Object>>> result = new DataResult<List<Map<String,Object>>>();
    	ToyDO findToy = new ToyDO();
    	findToy.setOwnerId(currentUser.getId());
    	List<ToyDO> resp = toyService.getAll(findToy);    	
    	List<Map<String,Object>> data = Lists.newArrayList();    	
    	for(ToyDO toy : resp) {
    		Map<String,Object> item = Maps.newTreeMap();
    		item.put("toySN", toy.getToySN());
    		item.put("toyName", toy.getToyName());
    		item.put("kidName", toy.getKidName());
    		if(null != toy.getKidGender()){
    			item.put("kidGender", toy.getKidGender());
    		}
    		if(null != toy.getKidBirth()){
    			item.put("kidBirth", toy.getKidBirth());
    		}
    		item.put("ownerId", toy.getOwnerId());
    		if(null != toy.getActivatorId()) {
    			item.put("activatorId", toy.getActivatorId());
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
    	DataResult<List<Map<String,Object>>> result = new DataResult<List<Map<String,Object>>>();

    	List<MyFriendDO>  resp = accountService.getMyFriends(currentUser.getId());
    	List<Map<String,Object>> data = Lists.newArrayList();    	
    	for(MyFriendDO myFriend : resp) {
    		Map<String,Object> item = Maps.newTreeMap();
    		item.put("friendId",myFriend.getFriendId());
    		item.put("relation",myFriend.getRelation());
    		item.put("photo", myFriend.getPhoto());
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
    	DataResult<List<Map<String,Object>>> result = new DataResult<List<Map<String,Object>>>();
    	Long toyUserId= requestParams.getLong("toy_user_id");
    	if(null == toyUserId){
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请指定所管理toy的ID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	//是我管理的Toy吗？
    	if(!accountService.isMyToy(currentUser.getId(), toyUserId)){
             result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请所管理toy的ID无效！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}
    	//获取该toy的所有好友列表
    	List<MyFriendDO>  resp = accountService.getMyFriends(toyUserId);
    	List<Map<String,Object>> data = Lists.newArrayList();    	
    	for(MyFriendDO myFriend : resp) {
    		Map<String,Object> item = Maps.newTreeMap();
    		item.put("friendId",myFriend.getFriendId());
    		item.put("relation",myFriend.getRelation());
    		item.put("photo", myFriend.getPhoto());
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
    public void doRenameMyToyFriend(ParameterParser requestParams) throws IOException {
        Assert.notNull(currentUser, "用户未登录!");
        SimpleResult result = new SimpleResult();
        if(!StringUtils.isNumeric(requestParams.getString("toyUserId"))) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请指定所管理toy的ID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;
        }
        if(!StringUtils.isNumeric(requestParams.getString("friendId"))) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请指定所管理toy的friendId！");
            response.getWriter().println(JSON.toJSONString(result));
            return;
        }
        if(StringUtils.isEmpty(requestParams.getString("relation"))){
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请指定所管理名字！");
            response.getWriter().println(JSON.toJSONString(result));
            return;
        }
        Long toyUserId= requestParams.getLong("toyUserId");
        Long friendId= requestParams.getLong("friendId");
        String relation = requestParams.getString("relation");
        //是我管理的Toy吗？
        if(!accountService.isMyToyFriend(currentUser.getId(), toyUserId,friendId)){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请所管理toy的Friend无效！");
            response.getWriter().println(JSON.toJSONString(result));
            return;
        }
        boolean bOK = accountService.toRenameMyFriend(toyUserId, friendId, relation);
        result.setSuccess(bOK);
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
    	String type = requestParams.getString("type","Leader");
    	MyGroupDO.GroupType groupType = null;
    	try{
    		groupType = Enum.valueOf(MyGroupDO.GroupType.class, requestParams.getString("groupType"));
    	}catch(Exception e) {
        	groupType = null;
    	}
    	
    	List<MyGroupDO>  data =  null;
    	if("LEADER".equalsIgnoreCase(type)) {
	    	//获取自己创建的群    	
	    	data = groupService.getMyCreatedGroups(currentUser.getId(),groupType);
    	} else {//FOLLOWER
    		data = groupService.getMyJoinedGroups(currentUser.getId(),groupType);
    	}
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
    public void doSendNotification(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Long> result = new DataResult<Long>();

    	if(StringUtils.isBlank(requestParams.getString("acceptorId"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定接收人！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	
    	String	type			=	requestParams.getString("type",StationLetterDO.MediaType.TEXT.name()) ;
    	StationLetterDO.MediaType mediaType = null;
    	try {
    		mediaType = Enum.valueOf(StationLetterDO.MediaType.class, type);  
    	}catch (Exception e){
    		mediaType = StationLetterDO.MediaType.TEXT;
    	}
  	
    	int     acceptorType = requestParams.getInt("acceptorType",0);
    	long acceptorId		=	requestParams.getLong("acceptorId");
    	String content			= requestParams.getString("content");
    	String url						= requestParams.getString("url");
    	Integer duration	=	requestParams.getInt("duration",0);
    	if(!type.equalsIgnoreCase(StationLetterDO.MediaType.TEXT.name())) {
    		if(StringUtils.isBlank(url) || 0== duration) {
                result.setSuccess(false);
                result.setErrorCode(2000);
                result.setMessage("请文件及时长！");
                response.getWriter().println(JSON.toJSONString(result));
                return;   			
    		}
    	}
    	StationLetterDO letter = new StationLetterDO();
    	letter.setSenderId(currentUser.getId());
    	letter.setAcceptorType(acceptorType);
    	letter.setAcceptorId(acceptorId);
    	letter.setContent(content);
    	letter.setUrl(url);
    	letter.setDuration(duration);
    	letter.setType(mediaType);
    	result = stationLetterService.createLetter(letter);
    	
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
    	StationLetterDO searchDO = new StationLetterDO();
    	searchDO.setAcceptorType(2);
    	searchDO.setAcceptorId(groupId);
    	searchDO.setPage(page);
    	searchDO.setLimit(20);
    	
    	Long readerId= requestParams.getLong("readerId",-1L);
    	if(-1L != readerId)
    		searchDO.setReaderId(readerId);
    	else
    		searchDO.setReaderId(currentUser.getId());
    	
    	Long time = requestParams.getLong("gmtCreated",-1L);
    	if(-1L != time) {
            try {           	
				searchDO.setGmtCreated(format.parse(format.format(time)));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	}
    	
    	result= stationLetterService.getMyLetters(searchDO);
    	
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
     * 标示消息已读
     * @param requestParams
     * @throws IOException
     */
    public void doReadNotification(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	SimpleResult result = new SimpleResult();

    	if(StringUtils.isBlank(requestParams.getString("letterId"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定消息ID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	Long letterId = requestParams.getLong("letterId");
    	StationLetterRecordDO record = new StationLetterRecordDO();
    	record.setReaderId(currentUser.getId());
    	record.setLetterId(letterId);
    	List<StationLetterRecordDO> resp = stationLetterRecordMapper.getAll(record);
    	if(CollectionUtils.isEmpty(resp)){
    		stationLetterRecordMapper.insert(record);
    	}
    	result.setSuccess(true);
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
    
    public void doGetLastItemsBySenderIds(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Map<String,Object>> result = new DataResult<>();
    	if(StringUtils.isBlank(requestParams.getString("senderIds"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指定senderID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	Iterable<String> split = Splitter.onPattern("[,|，]")
    			.omitEmptyStrings()
    			.split(requestParams.getString("senderIds"));
    	List<Long> senderIds = Lists.newArrayList();
    	for(String item : split) {
    		senderIds.add(Long.valueOf(item));
    	}
    	Integer acceptorType = requestParams.getInt("acceptorType",-1);   	
    	Long acceptorId = requestParams.getLong("acceptorId",-1);
    	if(-1 == acceptorType) acceptorType = null;
    	if(-1 == acceptorId) acceptorId = null;
    	result = stationLetterService.getLastItemsBySenderIds(senderIds, acceptorType, acceptorId);
    	response.getWriter().println(JSON.toJSONString(result));
        return;
    }
    public void doGetLastNotifications(ParameterParser requestParams) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Map<String,Object>> result = new DataResult<>();
    	if(StringUtils.isBlank(requestParams.getString("acceptorId"))
    			&& StringUtils.isBlank(requestParams.getString("senderIds"))) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("senderIds和acceptorId不能同时为空！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    
    	}
    	List<Long> senderIds = null;
    	if(!StringUtils.isBlank(requestParams.getString("senderIds"))) {
        	Iterable<String> split = Splitter.onPattern("[,|，]")
        			.omitEmptyStrings()
        			.split(requestParams.getString("senderIds"));
        	senderIds = Lists.newArrayList();
        	for(String item : split) {
        		senderIds.add(Long.valueOf(item));
        	}   
    	}

    	Integer acceptorType = requestParams.getInt("acceptorType",-1);   	
    	Long acceptorId = requestParams.getLong("acceptorId",-1);
    	if(-1 == acceptorType) acceptorType = null;
    	if(-1 == acceptorId) acceptorId = null;
    	result = stationLetterService.getLastItemsBySenderIds(senderIds, acceptorType, acceptorId);
    	response.getWriter().println(JSON.toJSONString(result));
        return;
    }

	public void doAnalysis(@Param("q") String query) throws Exception {
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<List<String>> result = new DataResult<List<String>>();
		List<String> resp = solrOperator.anlysisSolrResult("account", query);
		result.setSuccess(true);
		result.setMessage("分词成功!");
		result.setData(resp);
		response.getWriter().println(JSON.toJSONString(result));
		return;
	}
    
	public void doAddFriend(@Param("userId") Long userId, 
			@Param("friendName")String friendName,
			@Param("relation")String relation,
			@Param("friendMobile") String friendMobile,
			@Param("groupType") Integer groupType
			) throws Exception{
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<Map<String, Object>> result  = new DataResult<>();
		Map<String, Object> map = new HashMap<>();
		if (userId == null || friendName == null || relation == null || friendMobile == null || groupType == null) {
			result.setSuccess(false);
			result.setMessage("invalid request params");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		try {
			UserDO user = accountService.getUser(userId);
			//检查是否已是我的好友
			UserDO friendUser = accountService.getMyFriendByHisMobile(userId, friendMobile);
			if (friendUser != null) {
				map.put("friendId", friendUser.getId());
				result.setSuccess(false);
				result.setMessage("该用户已是你的好友");
				result.setData(map);
				response.getWriter().println(JSON.toJSONString(result));
				return;
			}
			//检查是否存在该账号，不存在则为其生成账号
			friendUser = accountService.getUserByMobile(friendMobile);
			if(friendUser == null){
				friendUser = new UserDO();
				friendUser.setLoginName(friendMobile);
				friendUser.setNickName(friendName);
				friendUser.setPassword("123456");
				friendUser.setType(AccountType.Mobile);
				friendUser.setComeFrom(AccoutChannel.Proxy);
				friendUser.setMobile(friendMobile);
				friendUser.setInvitorId(userId);
				Long id = accountService.register(friendUser);
				friendUser.setId(id);
			}
			//建立好友关系
			MyFriendDO myFriendDO = new MyFriendDO();
			myFriendDO.setFriendId(friendUser.getId());
			myFriendDO.setMyId(userId);
			myFriendDO.setGmtCreated(new Date());
			myFriendDO.setRelation(relation);
			accountService.addFriend(myFriendDO);
			
			//创建二人世界群
			Long groupId = 0L;
			if(groupType == GroupType.General.intValue()){
				groupId = groupService.createGroup(userId, friendMobile, GroupType.General);
				groupService.insertGroupMember(groupId, userId, user.getNickName());
				groupService.insertGroupMember(groupId, friendUser.getId(), friendMobile);
			}
			if(groupType == GroupType.Family.intValue()){
				MyGroupDO familyGroup = groupService.getMyFamilyGroup(userId);
				if(familyGroup != null){
					groupId = familyGroup.getId();
					groupService.insertGroupMember(groupId, friendUser.getId(), friendName);
				}else{
					result.setSuccess(false);
					result.setMessage("家庭组不存在");
					response.getWriter().println(JSON.toJSONString(result));
					return;
				}
			}
			map.put("friendId", friendUser.getId());
			map.put("groupId", groupId);
			result.setData(map);
			result.setSuccess(true);
			response.getWriter().println(JSON.toJSONString(result));
			return;
		} catch (Exception e) {
			e.printStackTrace();
			result.setSuccess(false);
			result.setMessage("error");
			response.getWriter().println(JSON.toJSONString(result));
			log.error(e.getMessage());
			return;
		}
    }
}

