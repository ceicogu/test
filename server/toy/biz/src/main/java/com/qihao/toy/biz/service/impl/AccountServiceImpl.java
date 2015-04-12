package com.qihao.toy.biz.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qihao.shared.base.utils.CryptoCoder;
import com.qihao.shared.base.utils.MD5Algorithm;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.dal.domain.MyFriendDO;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.enums.AccountTypeEnum;
import com.qihao.toy.dal.enums.FriendStatusEnum;
import com.qihao.toy.dal.enums.GroupTypeEnum;
import com.qihao.toy.dal.enums.MediaTypeEnum;
import com.qihao.toy.dal.enums.RegFromEnum;
import com.qihao.toy.dal.enums.RegStatusEnum;
import com.qihao.toy.dal.enums.ToyStatusEnum;
import com.qihao.toy.dal.enums.VerifyCodeStatusEnum;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
import com.qihao.toy.dal.persistent.MyFriendMapper;
import com.qihao.toy.dal.persistent.UserMapper;
import com.qihao.toy.dal.persistent.VerifyCodeMapper;
@Slf4j
@Service
public class AccountServiceImpl implements AccountService{
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ToyService toyService;
	@Autowired
	private StationLetterService stationLetterService;
	@Autowired	
	private MyFriendMapper myFriendMapper;
	@Autowired
	private GroupService groupService;
	@Autowired
	private VerifyCodeMapper  verifyCodeMapper;
	@Autowired
	private GlobalConfig globalConfig;
	
	public UserDO login(String loginName, String password) {
		Preconditions.checkArgument(StringUtils.isNotBlank(loginName),"用户名不能为空");
		Preconditions.checkArgument(StringUtils.isNotBlank(password),"密码不能为空");
		String md5pwd;
		try {
			md5pwd = MD5Algorithm.digest(password,globalConfig.getMd5Key());
		} catch (Exception e) {
			throw new IllegalArgumentException("系统配置参数错误！");
		}
		return userMapper.login(loginName, md5pwd);
	}

	public Long register(UserDO user) {
		//1.参数校验
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getLoginName()),"用户名不能为空");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()),"密码不能为空");
		//Preconditions.checkArgument(StringUtils.isNotBlank(user.getNickName()),"昵称不能为空");
		Preconditions.checkArgument(null != user.getComeFrom(),"注册来源未指明");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getComeSN()),"SN不能为空");	
		Preconditions.checkArgument(null != user.getType(),"帐号类型未指定");
		
		if(null == user.getStatus()) user.setStatus(RegStatusEnum.Normal.numberValue());
		//2.检查是否已注册
		UserDO existUser = new UserDO();
		existUser.setLoginName(user.getLoginName());
		existUser.setIsDeleted(0);
		List<UserDO> resp = userMapper.getAll(existUser);
		if(!CollectionUtils.isEmpty(resp)){
			throw new IllegalArgumentException("帐号已注册，请更换帐号!");
		}
		//3.密码md5
		String md5pwd;
		try {
			md5pwd = MD5Algorithm.digest(user.getPassword(), globalConfig.getMd5Key());
			user.setPassword(md5pwd);
		} catch (Exception e) {
			throw new IllegalArgumentException("系统配置参数错误！");
		}		
		//4.注册（创建用户）
		userMapper.insert(user);//注册
		//5.根据注册帐号类型，进行设定
		//5-1若是Toy注册，就不做后继动作
		if(user.getType().equals(AccountTypeEnum.TOY_REG.numberValue())) {//Toy注册
	    	//激活Toy
	    	toyService.toAcitvateToy( user.getId(), user.getComeSN(), user.getLoginName());
			//创建一个家庭群
			Long groupId = groupService.createGroup(user.getId(),"我的家庭群", GroupTypeEnum.Family);
			groupService.insertGroupMember(groupId, user.getId(),  "宝贝");
			return user.getId();
		}		
		//5-2若是手机注册，就要进一步检查是否扫码注册，是就要看该码对应的Toy是否已激活，若无就激活
		if(user.getComeFrom().equals(RegFromEnum.Scan.numberValue())) {//扫描ToySN注册
			//获取Ｔｏｙ帐号
			String toySN = user.getComeSN();
			ToyDO toy = toyService.getItemByToySN(toySN);
			if(null == toy || toy.getStatus().equals(ToyStatusEnum.Initial.numberValue())) {
				return user.getId();
			}
			if(toy.getStatus().equals(ToyStatusEnum.Activated.numberValue())) {
				//认领Toy				
				toyService.toClaimToy(user.getId(), toy.getToySN());	
			}
			//加入Toy家庭群
			List<MyGroupDO> resp4 = groupService.getMyCreatedGroups(toy.getActivatorId(), GroupTypeEnum.Family);
			if(!CollectionUtils.isEmpty(resp4)){
				groupService.insertGroupMember(resp4.get(0).getId(), user.getId(), "妈妈");
			}			
			//验证码
			//与Toy互为好友
			this.focusA2B(toy.getActivatorId(), user.getId());
			this.focusA2B(user.getId(),toy.getActivatorId());
			return user.getId();					
		}
		else {//邀请注册用户,注册成功后邀请者和被邀请者互为好友 
			Long invitorId= null;
			VerifyCodeDO verifyCode = new VerifyCodeDO();
			verifyCode.setType(VerifyCodeTypeEnum.Reg_InviteCode.numberValue());
			verifyCode.setCode(user.getComeSN());
			verifyCode.setMobile(user.getMobile());
			verifyCode.setStatus(VerifyCodeStatusEnum.Initial.numberValue());
			List<VerifyCodeDO> resp2 = verifyCodeMapper.getAll(verifyCode);
			if(CollectionUtils.isEmpty(resp2)){
				return user.getId();
			}
			verifyCode = resp2.get(0);
			verifyCodeMapper.updateStatusById(verifyCode.getId(), VerifyCodeStatusEnum.Verified.numberValue());//标记为已验证，不得再次使用
			invitorId= verifyCode.getInvitorId();

			if(null != invitorId) {	//互相添加为好友
				this.focusA2B(invitorId, user.getId());
				this.focusA2B(user.getId(),invitorId);
			}			
		}
		return user.getId();
	}

	public boolean update(UserDO user) {
		return userMapper.update(user);
	}

	public UserDO getUser(long userId) {
		return userMapper.getById(userId);
	}
	public UserDO getUserByLoginName(String loginName) {
		return userMapper.getByLoginName(loginName);
	}	
	public List<UserDO>  getUserList(List<Long> userIds){
		if(CollectionUtils.isEmpty(userIds)){
			return null;
		}
		UserDO user = new UserDO();
		user.setUserIds(userIds);
		return userMapper.getAll(user);
	}
	/**
	 * A==>B 好友
	 * @param myId
	 * @param myFriendId
	 */
	private void focusA2B(long userAId, long userBId){
		MyFriendDO myFriend = new MyFriendDO();
		myFriend.setMyId(userAId);
		myFriend.setFriendId(userBId);
		List<MyFriendDO> resp = myFriendMapper.getAll(myFriend);
		boolean needPushMessage = false;
		if(CollectionUtils.isEmpty(resp)){
			myFriend.setGmtInvited(new Date());
			myFriend.setGmtConfirmed(new Date());
			myFriend.setStatus(FriendStatusEnum.IsFriend.numberValue());
			myFriendMapper.insert(myFriend);
			needPushMessage = true;
		}else {
			MyFriendDO dbMyFriend = resp.get(0);
			if(!dbMyFriend.getStatus().equals(FriendStatusEnum.IsFriend.numberValue())){
				myFriend.setStatus(FriendStatusEnum.IsFriend.numberValue());
				myFriend.setGmtConfirmed(new Date());
				myFriendMapper.update(myFriend);
				needPushMessage = true;
			}
		}
		if(needPushMessage) {//推送消息给userBId
			//调用miPush接口推送消息给userBId			
			UserDO userA =this.getUser(userAId);
			UserDO userB =this.getUser(userBId);
			if(null != userB && !StringUtils.isBlank(userB.getMiRegId())) {			
				StationLetterDO letter = new StationLetterDO();
				letter.setAcceptorId(userB.getId());
				letter.setAcceptorType(0);
				letter.setContent(userA.getNickName()+"成为您的好友!");
				letter.setSenderId(userA.getId());
				letter.setType(MediaTypeEnum.TEXT.numberValue());
				stationLetterService.createLetter(letter);
			}
		}
		
	}

	public String createAuthToken(UserDO user) {
		Map<String,Long> param = new HashMap<String,Long>();
		param.put("uid", user.getId());
		param.put("ts", System.currentTimeMillis());

		try {
			String json = JSON.toJSONString(param);
			return CryptoCoder.aesEncrypt(json, globalConfig.getAesKey() , globalConfig.getAesIv());
		} catch (Exception e) {
			throw new RuntimeException("加密失败!");
		}
	}

	public UserDO validateAuthToken(String authToken) {
		try {
			String json = CryptoCoder.aesDecrypt(authToken,  globalConfig.getAesKey() , globalConfig.getAesIv());
			Map<String, Object> param =  JSON.parseObject(json, new TypeReference<Map<String, Object>>() {});

			Long uid = Long.valueOf(param.get("uid").toString());
			//Long ts	= param.get("ts");
			UserDO user = this.getUser(uid);
			if(null == user) {
				throw new RuntimeException("用户不存在!");
			}
			return user;
		} catch(RuntimeException e) {
			throw e;
		}catch (Exception e) {		
			log.warn("验证失败！exception={}",e);
			throw new RuntimeException("验证失败! 原因："+e.getMessage());
		}		
	}

	public List<UserDO> getMyFriends(long myId) {
		MyFriendDO myFriend = new MyFriendDO();
		myFriend.setMyId(myId);
		List<MyFriendDO> resp  = myFriendMapper.getAll(myFriend);
		if(CollectionUtils.isEmpty(resp)) {
			return null;
		}
		List<Long> userIds = Lists.newArrayList();
		for(MyFriendDO friend : resp){
			userIds.add(friend.getFriendId());
		}
		UserDO user = new UserDO();
		user.setUserIds(userIds);
		List<UserDO> data = userMapper.getAll(user);
		return data;
	}


	public Boolean isMyFriend(long myId, long friendId) {
		MyFriendDO myFriend = new MyFriendDO();
		myFriend.setMyId(myId);
		myFriend.setFriendId(friendId);
		List<MyFriendDO> resp = myFriendMapper.getAll(myFriend);
		return !CollectionUtils.isEmpty(resp);
	}
}
