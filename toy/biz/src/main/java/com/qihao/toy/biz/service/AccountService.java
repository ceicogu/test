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

package com.qihao.toy.biz.service;

import java.util.List;

import com.qihao.toy.dal.domain.MyFriendDO;
import com.qihao.toy.dal.domain.UserDO;

/**
 * 有关用户的操作。
 *
 */
public interface AccountService {
    /**
     * 登录用户，如果用户名和密码正确，则返回相应的用户信息。
     *
     * @param userName   用户名
     * @param password 密码
     * @return 用户信息，如果用户名或密码不正确，则返回<code>null</code>
     */
    UserDO login(String loginName, String password);

    /**
     * 注册用户。
     *
     * @param user 用户对象
     */
    Long register(UserDO user);

    /**
     * 更新用户的信息。
     *
     * @param user 用户对象
     */
    boolean update(UserDO user);

    /**
     * 取得指定id的用户。
     *
     * @param userId 用户id
     * @return 指定id的用户
     */
    UserDO getUser(long userId);
    /**
     * 根据登录名获取用户信息
     * @param loginName
     * @return
     */
    UserDO getUserByLoginName(String loginName);
    /**
     * 获取指令用户ID列表的用户详情
     * @param userIds
     * @return
     */
    List<UserDO>  getUserList(List<Long> userIds);
    /**
     * 创建用户认证token
     * @param user
     * @return
     */
    String createAuthToken(UserDO user);
    /**
     * 认证token是否有效
     * @param authToken
     * @return
     */
    UserDO validateAuthToken(String authToken);

    /**
     * 获取我的好友列表
     * @param userId
     * @return
     */
    List<MyFriendDO> getMyFriends(long myId);
    /**
     * 获取我管理的toy列表
     * @param myId
     * @return
     */
    List<UserDO> getMyToys(long myId);
    /**
     * 判断某个toy是我管理的吗
     * @param myId
     * @param toyUserId
     * @return
     */
    Boolean isMyToy(long myId, long toyUserId);
    /**
     * 判断是否是我的好友
     * @param myId
     * @param firendId
     * @return
     */
    Boolean isMyFriend(long myId, long friendId);
    /**
     * 判断是否是我管理toy的好友
     * @param myId
     * @param toyUserId
     * @param firendId
     * @return
     */
    Boolean isMyToyFriend(long myId, long toyUserId, long friendId);
    /**
     * 获取自己所在家庭群ID
     * @param myId
     * @return
     */
    List<Long> getMyFamilyGroupId(long myId);
    /**
     * 修改我的好友名称
     * @param myId
     * @param friendId
     * @param relation
     * @return
     */
    Boolean toRenameMyFriend(long myId, long friendId, String relation);
    
}
