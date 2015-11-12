package com.qihao.toy.biz.service.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
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
import com.qihao.shared.base.utils.CryptoCoder;
import com.qihao.shared.base.utils.MD5Algorithm;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.biz.solr.domain.AccountSolrDO;
import com.qihao.toy.biz.utils.ObjectDynamicCreator;
import com.qihao.toy.dal.domain.MyFriendDO;
import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.persistent.MyFriendMapper;
import com.qihao.toy.dal.persistent.MyGroupMemberMapper;
import com.qihao.toy.dal.persistent.UserMapper;
import com.qihao.toy.dal.persistent.VerifyCodeMapper;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ToyService toyService;
    @Autowired
    private StationLetterService stationLetterService;
    @Autowired
    private MyFriendMapper myFriendMapper;
    @Autowired
    private MyGroupMemberMapper myGroupMemberMapper;
    @Autowired
    private GroupService groupService;
    @Autowired
    private VerifyCodeMapper verifyCodeMapper;
    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private DefaultSolrOperator solrOperator;

    public UserDO login(String loginName, String password) {
        Preconditions.checkArgument(StringUtils.isNotBlank(loginName), "用户名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(password), "密码不能为空");
        String md5pwd;
        try {
            md5pwd = MD5Algorithm.digest(password, globalConfig.getMd5Key());
        } catch (Exception e) {
            throw new IllegalArgumentException("系统配置参数错误！");
        }
        return userMapper.login(loginName, md5pwd);
    }

    public Long register(UserDO user) {
        //1.参数校验(必填参数）
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getLoginName()), "用户名不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(user.getPassword()), "密码不能为空");
        Preconditions.checkArgument(null != user.getType(), "帐号类型未指定");
        Preconditions.checkArgument(null != user.getComeFrom(), "注册途径未指定");

        UserDO.AccoutChannel accountChannel = user.getComeFrom();
        if (null == accountChannel) {
            if (null == accountChannel) {
                throw new IllegalArgumentException("注册途径错误！");
            }
        }
        //UserDO.AccountType accountType = IntEnumUtils.valueOf(UserDO.AccountType.class, user.getType());
        UserDO.AccountType accountType = user.getType();
        if (accountType.equals(UserDO.AccountType.Toy)) {//故事机器注册
            Preconditions.checkArgument(StringUtils.isNotBlank(user.getComeSN()), "SN不能为空");//ToySN
            Preconditions.checkArgument(null != user.getInvitorId(), "非法注册（无邀请者 ）");
            user.setComeFrom(UserDO.AccoutChannel.Self);//toy都是自我注册
            //检查邀请者是否存在
            UserDO invitor = userMapper.getById(user.getInvitorId());
            if (null == invitor) {
                throw new IllegalArgumentException("非法注册（邀请者非法）");
            }
        } else if (accountType.equals(UserDO.AccountType.Mobile)) {//手机器注册
            Preconditions.checkArgument(null != user.getMobile(), "手机号码不可空");

            if (accountChannel.equals(UserDO.AccoutChannel.Invite)) {//邀请注册途径
                Preconditions.checkArgument(null != user.getComeSN(), "请填写邀请码");
                //检查邀请码是否有效
                VerifyCodeDO verifyCode = new VerifyCodeDO();
                verifyCode.setType(VerifyCodeDO.VerifyCodeType.Reg_InviteCode);
                verifyCode.setCode(user.getComeSN());
                verifyCode.setMobile(user.getMobile());
                verifyCode.setStatus(VerifyCodeDO.VerifyCodeStatus.Initial);
                List<VerifyCodeDO> resp2 = verifyCodeMapper.getAll(verifyCode);
                if (CollectionUtils.isEmpty(resp2)) {
                    throw new IllegalArgumentException("邀请码无效");
                }
                verifyCode = resp2.get(0);
                user.setInvitorId(verifyCode.getInvitorId());
            } else if (accountChannel.equals(UserDO.AccoutChannel.Scan)) {//扫码注册途径
                Preconditions.checkArgument(null != user.getComeSN(), "请正确扫描故事机二维码");
                //检查故事机是否已注册
                ToyDO toy = toyService.getItemByToySN(user.getComeSN());
                if (null != toy && null != toy.getActivatorId()) {
                    //已注册（注册成功后需将手机注册者加入故事机的家庭群，并成为好友）
                    user.setInvitorId(toy.getActivatorId());
                } else {
                    //未注册或未激活
                    throw new IllegalArgumentException("故事机未激活，不能扫码注册");
                }
            } else if (accountChannel.equals(UserDO.AccoutChannel.Proxy)) {//代理toy直接邀请注册
            	
            } else {//自主注册
                //do nothing
                user.setInvitorId(null);
            }
        } else {
            throw new IllegalArgumentException("请指定注册类型");
        }
        //设定账号状态初始值
        user.setStatus(UserDO.AccoutStatus.Normal);
        //2.检查是否已注册
        UserDO existUser = new UserDO();
        existUser.setLoginName(user.getLoginName());
        existUser.setIsDeleted(0);
        List<UserDO> resp = userMapper.getAll(existUser);
        if (!CollectionUtils.isEmpty(resp)) {
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
        if (user.getType().equals(UserDO.AccountType.Toy.intValue())) {//Toy注册
            //toy自己激活Toy
            toyService.toAcitvateToy(user.getId(), user.getComeSN(), user.getLoginName());
            //激活者认领toy
            UserDO invitor = this.getUser(user.getInvitorId());
            toyService.toClaimToy(user.getInvitorId(), user.getComeSN());
            //创建一个家庭群
            Long groupId = groupService.createGroup(user.getId(), "我的家庭群", MyGroupDO.GroupType.Family);
            groupService.insertGroupMember(groupId, user.getId(), user.getNickName(),user.getPhoto());
            groupService.insertGroupMember(groupId, invitor.getId(),invitor.getNickName(),invitor.getPhoto());
            //与Toy互为好友
            this.focusA2B(user.getInvitorId(), user.getId());
            this.focusA2B(user.getId(), user.getInvitorId());
            return user.getId();
        }
        //5-2若是手机注册，就要进一步检查是否扫码注册，是就要看该码对应的Toy是否已激活，若无就激活
        if (accountChannel.equals(UserDO.AccoutChannel.Scan)) {//扫描ToySN注册
            //获取Ｔｏｙ帐号
            String toySN = user.getComeSN();
            ToyDO toy = toyService.getItemByToySN(toySN);
            if (null == toy || toy.getStatus().equals(ToyDO.ToyStatus.Initial)) {
                return user.getId();
            }
            if (toy.getStatus().equals(ToyDO.ToyStatus.Activated)) {
                //认领Toy
                toyService.toClaimToy(user.getId(), toy.getToySN());
            }
            //加入Toy家庭群
            List<MyGroupDO> resp4 = groupService.getMyCreatedGroups(toy.getActivatorId(), MyGroupDO.GroupType.Family);
            if (!CollectionUtils.isEmpty(resp4)) {
                groupService.insertGroupMember(resp4.get(0).getId(), user.getId(), user.getNickName());
            }
            //验证码
            //与Toy互为好友
            this.focusA2B(toy.getActivatorId(), user.getId());
            this.focusA2B(user.getId(), toy.getActivatorId());
            return user.getId();
        } else if (accountChannel.equals(UserDO.AccoutChannel.Invite)) {//邀请注册用户,注册成功后邀请者和被邀请者互为好友
            Long invitorId = null;
            VerifyCodeDO verifyCode = new VerifyCodeDO();
            verifyCode.setType(VerifyCodeDO.VerifyCodeType.Reg_InviteCode);
            verifyCode.setCode(user.getComeSN());
            verifyCode.setMobile(user.getMobile());
            verifyCode.setStatus(VerifyCodeDO.VerifyCodeStatus.Initial);
            List<VerifyCodeDO> resp2 = verifyCodeMapper.getAll(verifyCode);
            if (CollectionUtils.isEmpty(resp2)) {
                return user.getId();
            }
            verifyCode = resp2.get(0);
            verifyCodeMapper.updateStatusById(verifyCode.getId(), VerifyCodeDO.VerifyCodeStatus.Verified);//标记为已验证，不得再次使用
            invitorId = verifyCode.getInvitorId();

            if (null != invitorId) {    //互相添加为好友
                this.focusA2B(invitorId, user.getId());
                this.focusA2B(user.getId(), invitorId);
                //获取邀请者管理的toy，并将刚注册用户成为toy的好友
                List<ToyDO> myManageToys = toyService.getMyManageToys(invitorId);
                if (!CollectionUtils.isEmpty(myManageToys)) {
                    for (ToyDO toy : myManageToys) {
                        this.focusA2B(toy.getActivatorId(), user.getId());
                        this.focusA2B(user.getId(), toy.getActivatorId());
                    }
                }
            }
        } else if (accountChannel.equals(UserDO.AccoutChannel.Proxy)) {//代理toy直接邀请注册
            //获取Ｔｏｙ帐号
            List<ToyDO> resp2 = toyService.getMyManageToys(user.getInvitorId());
            for(ToyDO toy : resp2) {
	            //加入Toy家庭群
	            List<MyGroupDO> resp4 = groupService.getMyCreatedGroups(toy.getActivatorId(), MyGroupDO.GroupType.Family);
	            if (!CollectionUtils.isEmpty(resp4)) {
	                groupService.insertGroupMember(resp4.get(0).getId(), user.getId(), user.getNickName());
	            }
	            //验证码
	            //与Toy互为好友
	            this.focusA2B(toy.getActivatorId(), user.getId());
	            this.focusA2B(user.getId(), toy.getActivatorId());
            }
            return user.getId();        	
        }else {//自主注册
            //do nothing
        }
        return user.getId();
    }

    public boolean update(UserDO user) {
        boolean bOK = userMapper.update(user);
        if(bOK && StringUtils.isBlank(user.getPhoto())){
        	
        	if(user.getId() != null) {
        		//修改对应的群昵称
	        	MyGroupMemberDO myGroupMemberDO = new MyGroupMemberDO();
	        	myGroupMemberDO.setMemberId(user.getId());
	        	myGroupMemberDO.setMemberPhoto(user.getPhoto());
	        	myGroupMemberMapper.update(myGroupMemberDO);
	        	//修改好友列表中的昵称
	        	MyFriendDO myFriendDO = new MyFriendDO();
	        	myFriendDO.setFriendId(user.getId());
	        	myFriendDO.setPhoto(user.getPhoto());
	        	myFriendMapper.update(myFriendDO);
        	}
        	
        	
        }
        return bOK;
    }

    public UserDO getUser(long userId) {
        return userMapper.getById(userId);
    }

    public UserDO getUserByLoginName(String loginName) {
        return userMapper.getByLoginName(loginName);
    }

    public List<UserDO> getUserList(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        UserDO user = new UserDO();
        user.setUserIds(userIds);
        return userMapper.getAll(user);
    }

    /**
     * A==>B 好友
     *
     * @param userAId
     * @param userBId
     */
    private void focusA2B(long userAId, long userBId) {
        UserDO userA = this.getUser(userAId);
        UserDO userB = this.getUser(userBId);

        MyFriendDO myFriend = new MyFriendDO();
        myFriend.setMyId(userBId);
        myFriend.setFriendId(userAId);
        myFriend.setRelation(userA.getNickName());
        myFriend.setPhoto(userA.getPhoto());
        List<MyFriendDO> resp = myFriendMapper.getAll(myFriend);
        boolean needPushMessage = false;
        if (CollectionUtils.isEmpty(resp)) {
            myFriend.setGmtInvited(new Date());
            myFriend.setGmtConfirmed(new Date());
            myFriend.setStatus(MyFriendDO.FriendStatus.IsFriend);
            long id = myFriendMapper.insert(myFriend);
            {//添加到索引中
                AccountSolrDO solrDO = new AccountSolrDO();
                solrDO.setId(id);
                solrDO.setMyId(userBId);
                solrDO.setFriendId(userAId);
                solrDO.setRelation(myFriend.getRelation());
                try{
                    solrOperator.writeSolrDocument("account", ObjectDynamicCreator.getFieldVlaue(solrDO));
                } catch (Exception e){
                    //do nothing
                }
            }
            needPushMessage = true;
        } else {
            MyFriendDO dbMyFriend = resp.get(0);
            if (!dbMyFriend.getStatus().equals(MyFriendDO.FriendStatus.IsFriend)) {
                myFriend.setStatus(MyFriendDO.FriendStatus.IsFriend);
                myFriend.setGmtConfirmed(new Date());
                myFriendMapper.update(myFriend);
                needPushMessage = true;
            }
        }
        //TODO 若B有管理的Toy帐号，就同时将A变成Toy的好友

        if (needPushMessage) {//推送消息给userBId
            //调用miPush接口推送消息给userBId
            if (null != userB && !StringUtils.isBlank(userB.getDeviceToken())) {
                StationLetterDO letter = new StationLetterDO();
                letter.setAcceptorId(userB.getId());
                letter.setAcceptorType(0);
                letter.setContent(userA.getNickName() + "成为您的好友!");
                letter.setSenderId(userA.getId());
                letter.setType(StationLetterDO.MediaType.TEXT);
                stationLetterService.createLetter(letter);
            }
        }

    }

    public String createAuthToken(UserDO user) {
        Map<String, Long> param = new HashMap<String, Long>();
        param.put("uid", user.getId());
        param.put("ts", System.currentTimeMillis());

        try {
            String json = JSON.toJSONString(param);
            String aesStr = CryptoCoder.aesEncrypt(json, globalConfig.getAesKey(), globalConfig.getAesIv());
            return URLEncoder.encode(aesStr,"utf-8");
 //           return CryptoCoder.aesEncrypt(json, globalConfig.getAesKey(), globalConfig.getAesIv());
        } catch (Exception e) {
            throw new RuntimeException("加密失败!");
        }
    }

    public UserDO validateAuthToken(String authToken) {
        try {
        	//String decodeStr = URLDecoder.decode(authToken,"utf-8");
            //String json = CryptoCoder.aesDecrypt(decodeStr, globalConfig.getAesKey(), globalConfig.getAesIv());
        	String json = CryptoCoder.aesDecrypt(authToken, globalConfig.getAesKey(), globalConfig.getAesIv());
            Map<String, Object> param = JSON.parseObject(json, new TypeReference<Map<String, Object>>() {
            });

            Long uid = Long.valueOf(param.get("uid").toString());
            //Long ts	= param.get("ts");
            UserDO user = this.getUser(uid);
            if (null == user) {
                throw new RuntimeException("用户不存在!");
            }
            return user;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.warn("验证失败！exception={}", e);
            throw new RuntimeException("验证失败! 原因：" + e.getMessage());
        }
    }

    public List<MyFriendDO> getMyFriends(long myId) {
        MyFriendDO myFriend = new MyFriendDO();
        myFriend.setMyId(myId);
        List<MyFriendDO> resp = myFriendMapper.getAll(myFriend);
        return resp;

    }

    public Boolean isMyFriend(long myId, long friendId) {
        MyFriendDO myFriend = new MyFriendDO();
        myFriend.setMyId(myId);
        myFriend.setFriendId(friendId);
        List<MyFriendDO> resp = myFriendMapper.getAll(myFriend);
        return !CollectionUtils.isEmpty(resp);
    }

    public Boolean isMyToyFriend(long myId, long toyUserId, long friendId) {
        if (!this.isMyToy(myId, toyUserId)) {
            return false;
        }
        MyFriendDO myFriend = new MyFriendDO();
        myFriend.setMyId(toyUserId);
        myFriend.setFriendId(friendId);
        List<MyFriendDO> resp = myFriendMapper.getAll(myFriend);
        return !CollectionUtils.isEmpty(resp);
    }

    public List<MyGroupDO> getMyFamilyGroupId(long myId) {
        return groupService.getMyJoinedGroups(myId, MyGroupDO.GroupType.Family);
    }

    public List<UserDO> getMyToys(long myId) {
        List<Long> userIds = toyService.getMyToyUserIds(myId);
        if (CollectionUtils.isEmpty(userIds)) {
            return null;
        }
        UserDO user = new UserDO();
        user.setUserIds(userIds);

        return userMapper.getAll(user);
    }

    public Boolean isMyToy(long myId, long toyUserId) {
        List<Long> userIds = toyService.getMyToyUserIds(myId);
        if (CollectionUtils.isEmpty(userIds)) {
            return false;
        }
        return userIds.contains(toyUserId);
    }

    public Boolean toRenameMyFriend(long myId, long friendId, String relation) {
        try {
            MyFriendDO myFriendDO = myFriendMapper.getItemByFriendId(myId,friendId);
            if(null == myFriendDO) {
                return false;
            }
            boolean bOK = myFriendMapper.modifyRelation(myId, friendId, relation) > 0;
            if(bOK){
                AccountSolrDO solrDO = new AccountSolrDO();
                solrDO.setId(myFriendDO.getId());
                solrDO.setMyId(myId);
                solrDO.setFriendId(friendId);
                solrDO.setRelation(relation);

                try{
                    solrOperator.writeSolrDocument("account", ObjectDynamicCreator.getFieldVlaue(solrDO));
                }catch (Exception e){
                    //do nothing
                }
            }
            return bOK;
        } catch (Exception e) {
            return false;
        }
    }

}
