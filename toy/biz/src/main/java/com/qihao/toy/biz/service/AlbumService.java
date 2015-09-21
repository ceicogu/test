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

import com.qihao.toy.dal.domain.AlbumDO;
import com.qihao.toy.dal.domain.ResourceDO;

/**
 * 专辑管理
 */
public interface AlbumService {
	/**
	 * 添加
	 * @param album
	 * @return
	 */
	Long insert(AlbumDO album);
	/**
	 * 修改
	 * @param albumId
	 * @param album
	 * @return
	 */
	Boolean update(Long albumId, AlbumDO album);
	/**
	 * 逻辑删除
	 * @param albumId
	 * @return
	 */
	Boolean deleteById(Long albumId);
	/**
	 *  获取单ID
	 * @param albumId
	 * @return
	 */
	AlbumDO getById(Long albumId);
	/**
	 * 批量获取
	 * @param album
	 * @return
	 */
	List<AlbumDO> getAll(AlbumDO album);
	/**
	 * 根据标签ID获取对应资源ID列表
	 * @param tagId
	 * @return
	 */
	List<Long> getAlbumIdsByTag(Long tagId);
	/**
	 * 根据标签ID获取对应资源列表(分页)
	 * @param albumId
	 * @return
	 */
	List<AlbumDO> getAlbumsByTag(Long albumId);
	/**
	 * 根据标签ID获取对应资源列表(分页)
	 * @param tagId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	List<AlbumDO> getAlbumsByTag(Long tagId, Integer page, Integer pageSize);
	/**
	 * 根据专辑ID获取资源表
	 * @param albumId
	 * @return
	 */
	List<ResourceDO> getResourcesByAlbumId(Long albumId);
}
