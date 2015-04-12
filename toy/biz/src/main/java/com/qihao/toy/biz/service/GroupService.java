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

import com.qihao.toy.dal.domain.MyGroupDO;
import com.qihao.toy.dal.domain.MyGroupMemberDO;
import com.qihao.toy.dal.enums.GroupTypeEnum;

/**
 * 有关用户的操作。
 *
 */
public interface GroupService {
	/**
	 * 创建
	 * @param groupName
	 * @param groupType
	 * @return
	 */
	Long createGroup(long userId, String groupName, GroupTypeEnum groupType);
	/**
	 * 更新
	 * @param group
	 * @return
	 */
    boolean update(MyGroupDO group);
    /**
     * 删除
     * @param groupId
     * @return
     */
    boolean delete(long groupId);
    /**
     * 根据ＩＤ获取单条信息
     * @param groupId
     * @return
     */
    MyGroupDO getItemById(long groupId);
    /**
     * 判断是否是该群成员
     * @param groupId
     * @param userId
     * @return
     */
    boolean isGroupMember(long groupId, long userId);    
    /**
     * 添加群成员
     * @param groupId
     * @param memebrId
     * @param memberName　　可空
     * @return
     */
    Long insertGroupMember(long groupId, long memberId, String memberName);
    /**
     * 获取我创建的群
     * @param myId
     * @return
     */
    List<MyGroupDO> getMyCreatedGroups(long myId, GroupTypeEnum groupType);
    /**
     * 获取我加入的群
     * @param myId
     * @return
     */
    List<MyGroupDO> getMyJoinedGroups(long myId);
    
    /**
     * 获取指定群的所有成员
     * @param myGroupId
     * @return
     */
    List<MyGroupMemberDO> getGroupMembersByGroupId(long groupId);
    /**
     * 获取群成员用户ID列表
     * @param groupId
     * @return
     */
    List<Long> getUserIdsByGroupId(long groupId);
}
