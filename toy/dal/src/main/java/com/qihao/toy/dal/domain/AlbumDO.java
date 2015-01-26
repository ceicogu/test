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

package com.qihao.toy.dal.domain;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
/**
 * 专辑表（故事合集＆歌曲专辑）
 * @author luqiao
 *
 */
@Setter @Getter
public class AlbumDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	
	private String	title;					//专题名称
    private String	summary;		//摘要
    private String	photo;				//封面
    private Long		creatorId;		//创建者ID
    
    //查询
    private List<Long>  albumIds;
}
