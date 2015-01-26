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
import java.util.Map;

import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.domain.ToyDO;
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
     * 根据toySN获取其归属ownerId
     * @param toySN
     * @return
     */
    ToyDO getToyBySN(String toySN);
    /**
     * 给Toy及宝宝命名
     * @param userId  用户ID
     * @param toySN   故事机唯一编码
     * @param toyName   可选　　 故事机名称
     * @param kidParams 可选			宝宝名称等信息
     * @return
     */
    ToyDO renameToy(long userId, String toySN,String toyName, Map<String, Object> kidParams);
    /**
     * 获取我的Toy列表
     * @param toy
     * @return
     */
    List<ToyDO> getMyToys(long myId);
    /**
     * 获取我的好友列表
     * @param userId
     * @return
     */
    List<UserDO> getMyFriends(long myId);
    /**
     * 获取我的群
     * @param type      类型:0-我管理的群/1-我参加的群
     * @param myId
     * @return
     */
    List<MyGroupDO> getMyGroups(int type, long myId);

    /**
     * 获取我某个群所有成员
     * @param myGroupId
     * @return
     */
    List<MyGroupMemberDO> getMyGroupMembers(long myId, long myGroupId);
    /**
     * 判断是否是该群成员
     * @param groupId
     * @param userId
     * @return
     */
    Boolean isGroupMember(long groupId, long userId);
}
