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

import com.qihao.toy.dal.domain.ResourceDO;

/**
 * 资源管理
 */
public interface ResourceService {
	/**
	 * 添加
	 * @param tagType
	 * @param tagName
	 * @return
	 */
	Long insert(ResourceDO resourc);
	/**
	 * 修改
	 * @param resouceId
	 * @param resource
	 * @return
	 */
	Boolean update(Long resourceId, ResourceDO resource);
	/**
	 * 逻辑删除
	 * @param resourceId
	 * @return
	 */
	Boolean deleteById(Long resouceId);
	/**
	 *  获取单ID
	 * @param resouceId
	 * @return
	 */
	ResourceDO getById(Long resouceId);
	/**
	 * 批量获取
	 * @param resource
	 * @return
	 */
	List<ResourceDO> getAll(ResourceDO tag);
	/**
	 * 根据标签ID获取对应资源ID列表
	 * @param tagId
	 * @return
	 */
	List<Long> getReosurceIdsByTag(Long tagId);
	/**
	 * 根据标签ID获取对应资源列表(分页)
	 * @param tagId
	 * @return
	 */
	List<ResourceDO> getReosurcesByTag(Long tagId);
	/**
	 * 根据标签ID获取对应资源列表(分页)
	 * @param tagId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<ResourceDO> getReosurcesByTag(Long tagId, Integer page, Integer pageSize);
}
