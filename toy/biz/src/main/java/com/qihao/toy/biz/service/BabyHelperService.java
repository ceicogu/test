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

import com.qihao.toy.dal.domain.BabyHelperDO;

/**
 * 助手
 */
public interface BabyHelperService {
	/**
	 * 添加
	 * @return
	 */
	Long insert(BabyHelperDO helper);
	/**
	 * 修改
	 * @return
	 */
	Boolean update(Long id, BabyHelperDO helper);
	/**
	 * 逻辑
	 * @return
	 */
	Boolean deleteById(Long id);
	/**
	 *  获取单ID
	 * @return
	 */
	BabyHelperDO getById(Long id);
	/**
	 * 批量获取
	 * @return
	 */
	List<BabyHelperDO> getAll(BabyHelperDO helper);
}
