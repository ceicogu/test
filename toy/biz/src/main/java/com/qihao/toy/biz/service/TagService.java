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

import com.qihao.toy.dal.domain.TagDO;

/**
 * 标签管理
 */
public interface TagService {
	/**
	 * 添加新标签
	 * @param tagType
	 * @param tagName
	 * @return
	 */
	Long insert(int tagType, String tagName);
	Long insert(int tagType, String tagName, Long tagParentId);
	/**
	 * 修改指定ID的标签名称等
	 * @param tagId
	 * @param tag
	 * @return
	 */
	Boolean update(Long tagId, TagDO tag);
	/**
	 * 根据ID逻辑删除标签
	 * @param tagId
	 * @return
	 */
	Boolean deleteById(Long tagId);
	/**
	 *  获取单ID标签信息
	 * @param tagId
	 * @return
	 */
	TagDO getById(Long tagId);
	/**
	 * 批量获取标签列表
	 * @param tag
	 * @return
	 */
	List<TagDO> getAll(TagDO tag);
}
