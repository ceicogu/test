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

import com.qihao.toy.dal.domain.ToyDO;

/**
 * 故事机管理
 */
public interface ToyService {
	Long insert(ToyDO toy);
	
	ToyDO getItemByToySN(String toySN);
	List<ToyDO> getAll(ToyDO toy);
	
	Boolean update(String toySN, ToyDO toy);
	
	Boolean toAcitvateToy( long activatorId,String toySN, String toyMac);
	
	ToyDO toClaimToy(long ownerId, String toySN);
	
	ToyDO toNameToy(long ownerId, String toySN, String toyName, String kidName, Integer kidGender, Integer kidAge, Date kidBirth);
	ToyDO toNameToy(long ownerId, String toySN, String toyName, Map<String, String> kidParams);
}
