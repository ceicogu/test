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
import com.qihao.toy.biz.utils.MiPushUtils;
import com.qihao.toy.dal.domain.MyFriendDO;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.domain.MyToyDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.enums.FriendStatusEnum;
import com.qihao.toy.dal.enums.GroupTypeEnum;
import com.qihao.toy.dal.enums.RegFromEnum;
import com.qihao.toy.dal.enums.RegStatusEnum;
import com.qihao.toy.dal.enums.ToyStatusEnum;
import com.qihao.toy.dal.enums.VerifyCodeStatusEnum;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
import com.qihao.toy.dal.persistent.MyFriendMapper;
import com.qihao.toy.dal.persistent.MyGroupMapper;
import com.qihao.toy.dal.persistent.MyGroupMemberMapper;
import com.qihao.toy.dal.persistent.MyToyMapper;
import com.qihao.toy.dal.persistent.ToyMapper;
import com.qihao.toy.dal.persistent.UserMapper;
import com.qihao.toy.dal.persistent.VerifyCodeMapper;
import com.xiaomi.xmpush.server.Message;
@Slf4j
@Service
public class AccountServiceImpl implements AccountService{
	@Autowired
	private UserMapper userMapper;
	@Autowired
	private ToyMapper toyMapper;
	@Autowired
	private MyToyMapper myToyMapper;
	@Autowired
	private MyFriendMapper myFriendMapper;
	@Autowired
	private MyGroupMapper myGroupMapper;
	@Autowired
	private MyGroupMemberMapper myGroupMemberMapper;
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
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getLoginName()),"用户名不能为空");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()),"密码不能为空");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getNickName()),"昵称不能为空");
		Preconditions.checkArgument(null != user.getComeFrom(),"注册来源未指明");
		Preconditions.checkArgument(StringUtils.isNotBlank(user.getComeSN()),"SN不能为空");	
		Preconditions.checkArgument(null != user.getType(),"帐号类型未指定");
		
		if(null == user.getStatus()) user.setStatus(RegStatusEnum.Normal.numberValue());
		{//检查是否已注册
			UserDO existUser = new UserDO();
			existUser.setLoginName(user.getLoginName());
			existUser.setIsDeleted(0);
			List<UserDO> resp = userMapper.getAll(existUser);
			if(!CollectionUtils.isEmpty(resp)){
				throw new IllegalArgumentException("帐号已注册，请更换帐号!");
			}
		}
		String md5pwd;
		try {
			md5pwd = MD5Algorithm.digest(user.getPassword(), globalConfig.getMd5Key());
		} catch (Exception e) {
			throw new IllegalArgumentException("系统配置参数错误！");
		}	
		
		user.setPassword(md5pwd);
		userMapper.insert(user);//注册
		
		if(user.getComeFrom().equals(RegFromEnum.Scan.numberValue())) {//扫描ToySN注册
			String toySN = user.getComeSN();
			ToyDO toy = toyMapper.getItemByToySN(toySN);
			//若Toy未激活，就激活该Toy并设定Toy的监管人是自己
			//否则，就将自己与该Toy的监管人互相设置为好友
			if(toy.getStatus().equals(ToyStatusEnum.Initial.numberValue())) {
				//激活Toy
				this.activeToy(user, toy);
				return user.getId();
			}
			else{
				if(toy.getOwnerId().equals(user.getId())) {//是Toy的激活人，就直接返回
					return user.getId();
				}
				//加入Toy－Owner的家庭群
				MyGroupDO myGroup = new MyGroupDO();
				myGroup.setMyId(toy.getOwnerId());
				myGroup.setGroupType(GroupTypeEnum.Family.numberValue());
				List<MyGroupDO> resp = myGroupMapper.getAll(myGroup);
				if(CollectionUtils.isEmpty(resp)) {
						throw new IllegalArgumentException("用户群不存在！");
				}
				myGroup = resp.get(0);			
				MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
				myGroupMember.setGroupId(myGroup.getId());
				myGroupMember.setMemberId(user.getId());
				myGroupMember.setMemberName(user.getNickName());
				myGroupMemberMapper.insert(myGroupMember);
				return user.getId();
			}			
		}else {//彼此成为好友 
			Long invitorId= null;
			if(user.getType() == 0 ) {
				//获取邀请码
				VerifyCodeDO verifyCode = new VerifyCodeDO();
				verifyCode.setType(VerifyCodeTypeEnum.Reg_InviteCode.numberValue());
				verifyCode.setCode(user.getComeSN());
				verifyCode.setMobile(user.getMobile());
				verifyCode.setStatus(VerifyCodeStatusEnum.Initial.numberValue());
				List<VerifyCodeDO> resp = verifyCodeMapper.getAll(verifyCode);
				if(CollectionUtils.isEmpty(resp)){
					return user.getId();
				}
				verifyCode = resp.get(0);
				verifyCodeMapper.updateStatusById(verifyCode.getId(), VerifyCodeStatusEnum.Verified.numberValue());//标记为已验证，不得再次使用
				invitorId= verifyCode.getInvitorId();
			}else {
				invitorId = user.getInvitorId();
			}
			if(null != invitorId) {	//互相添加为好友
				this.focusA2B(invitorId, user.getId());
				this.focusA2B(user.getId(),invitorId);
			}			
		}
		return user.getId();
	}

	private boolean activeToy(UserDO user,  ToyDO toy) {
		toy.setStatus(ToyStatusEnum.Activated.numberValue());
		toy.setOwnerId(user.getId());
		toy.setGmtOwned(new Date());
		toyMapper.update(toy);
		//注册Toy到用户表
		UserDO userForToy = new UserDO();
		userForToy.setComeFrom(RegFromEnum.Invite.numberValue());
		userForToy.setComeSN(toy.getToySN());
		userForToy.setInvitorId(user.getId());//邀请者
		userForToy.setLoginName("toy_"+toy.getToySN());
		userForToy.setPassword(toy.getToySN());
		userForToy.setNickName(toy.getToySN());
		userForToy.setType(1);//帐号类型:1-TOY注册
		userForToy.setStatus(RegStatusEnum.Normal.numberValue());
		 this.register(userForToy);
		 //加入玩具用户表
		MyToyDO myToy = new MyToyDO();
		myToy.setMyId(user.getId());
		myToy.setToySN(toy.getToySN());				
		myToyMapper.insert(myToy);
		//创建我的家庭
		MyGroupDO myGroup = new MyGroupDO();
		myGroup.setGroupName("我的家庭群");
		myGroup.setGroupType(GroupTypeEnum.Family.numberValue());
		myGroup.setMyId(user.getId());
		myGroupMapper.insert(myGroup);
		//将自己添加为家庭成员
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setGroupId(myGroup.getId());
		myGroupMember.setMemberId(user.getId());
		myGroupMember.setMemberName(user.getNickName());
		myGroupMemberMapper.insert(myGroupMember);
		//将Toy用户自动添加成我的家庭群中来
		myGroupMember.setGroupId(myGroup.getId());
		myGroupMember.setMemberId(userForToy.getId());
		myGroupMember.setMemberName(userForToy.getNickName());
		myGroupMemberMapper.insert(myGroupMember);
		return true;
	}
	public boolean update(UserDO user) {
		return userMapper.update(user);
	}

	public UserDO getUser(long userId) {
		return userMapper.getById(userId);
	}
	public List<UserDO>  getUserList(List<Long> userIds){
		if(CollectionUtils.isEmpty(userIds)){
			return null;
		}
		UserDO user = new UserDO();
		user.setUserIds(userIds);
		return userMapper.getAll(user);
	}
	public ToyDO getToyBySN(String toySN) {
		return toyMapper.getItemByToySN(toySN);
	}
	public ToyDO renameToy(long userId, String toySN, String toyName, Map<String, Object> kidParams) {
		ToyDO toy = this.getToyBySN(toySN);		
		Preconditions.checkArgument(null != toy, "SN参数错误,toySN=%s",toySN);
		UserDO user = this.getUser(userId);
		Preconditions.checkArgument(null != user,"用户不存在,userId=%s",userId);
		Preconditions.checkArgument(toy.getOwnerId().equals(userId),"您不是故事机的监护人,userId=%s,toySN=%s",userId,toySN);

		Preconditions.checkArgument(StringUtils.isNotBlank(toyName), "玩具姓名不能为空");
		toy.setToyName(toyName);
		if(kidParams.containsKey("kidName")) toy.setKidName(kidParams.get("kidName").toString());
		if(kidParams.containsKey("kidAge")) toy.setKidAge((Integer)kidParams.get("kidAge"));
		if(kidParams.containsKey("kidGender")) toy.setKidGender((Integer)kidParams.get("kidGender"));
		if(kidParams.containsKey("kidBirth")) toy.setKidBirth((Date)kidParams.get("kidBirth"));
		toyMapper.update(toy);
		return toy;
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
				try {
					Message message = MiPushUtils.buildMessage("好友", userA.getNickName()+"成为您的好友!", "关注好友消息");
					MiPushUtils.sendMessage(message, userB.getMiRegId());
				} catch (Exception e) {
					log.error("推送消息异常!e={}",e.getMessage());
				}				
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
				return null;
			}
			return user;
		} catch (Exception e) {
			log.warn("验证失败！exception={}",e);
			//throw new RuntimeException("验证失败!");
			return null;
		}		
	}

	public List<ToyDO> getMyToys(long myId) {
		ToyDO toy = new ToyDO();
		toy.setOwnerId(myId);
		
		return toyMapper.getAll(toy);
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

	public List<MyGroupDO> getMyGroups(int type,long myId) {
		MyGroupDO myGroup = new MyGroupDO();
		if(type ==0) {
			myGroup.setMyId(myId);
		}else {
			MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
			myGroupMember.setMemberId(myId);
			List<MyGroupMemberDO> resp = myGroupMemberMapper.getAll(myGroupMember);
			if(CollectionUtils.isEmpty(resp)){
				return null;
			}
			List<Long> groupIds = Lists.newArrayList();
			for(MyGroupMemberDO member : resp) {
				groupIds.add(member.getGroupId());
			}
			myGroup.setGroupIds(groupIds);
		}
		return myGroupMapper.getAll(myGroup);
	}
	public List<MyGroupMemberDO> getMyGroupMembers(long myId, long myGroupId) {
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setMemberId(myId);
		myGroupMember.setGroupId(myGroupId);
		return myGroupMemberMapper.getAll(myGroupMember);
	}
	public List<Long>  getAllUserIdsByGroupId(Long groupId){
		MyGroupMemberDO myGroupMember = new MyGroupMemberDO();
		myGroupMember.setGroupId(groupId);
		List<MyGroupMemberDO> resp = myGroupMemberMapper.getAll(myGroupMember);
		List<Long>  userIds = Lists.newArrayList();
		for(MyGroupMemberDO member : resp){
			userIds.add(member.getMemberId());
		}
		return userIds;
	}
	public Boolean isGroupMember(long groupId, long userId) {
		MyGroupMemberDO member = new MyGroupMemberDO();
		member.setGroupId(groupId);
		member.setMemberId(userId);
		List<MyGroupMemberDO> resp = myGroupMemberMapper.getAll(member);
		return !CollectionUtils.isEmpty(resp); 
	}
}
