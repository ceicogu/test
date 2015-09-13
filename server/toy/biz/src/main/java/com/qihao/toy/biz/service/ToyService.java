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

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.qihao.toy.dal.domain.MyToyDO;
import com.qihao.toy.dal.domain.ToyDO;

/**
 * 故事机管理
 */
public interface ToyService {
	Long insert(ToyDO toy);
	
	ToyDO getItemByToySN(String toySN);
	List<ToyDO> getAll(ToyDO toy);
	
	Boolean update(String toySN, ToyDO toy);
	/**
	 * 激活故事机
	 * @param activatorId
	 * @param toySN
	 * @param toyMac
	 * @return
	 */
	Boolean toAcitvateToy( long activatorId,String toySN, String toyMac);
	/**
	 * 认领故事机
	 * @param ownerId
	 * @param toySN
	 * @return
	 */
	ToyDO toClaimToy(long ownerId, String toySN);
	/**
	 * 获取我认领的故事机列表
	 * @param ownerId
	 * @return
	 */
	List<ToyDO> getMyClaimToys(long ownerId);
	/**
	 * 获取我的故事机注册ID列表
	 * @param myId
	 * @return
	 */
	List<Long> getMyToyUserIds(long myId);
	/**
	 * 获取我管理的故事机列表
	 * @param myId
	 * @return
	 */
	List<ToyDO> getMyManageToys(long myId);
    ToyDO getMyManageToy(long myId, long toyUserId);
}
