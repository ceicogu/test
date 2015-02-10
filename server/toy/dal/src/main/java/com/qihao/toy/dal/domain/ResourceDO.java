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
 * 资源表
 * @author luqiao
 *
 */
@Setter @Getter
public class ResourceDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	private Long		id;
	private String	title;					//主标题
	private String	subTitle;			//副标题
	private String	author;				//作者
	private String	composer;		//作曲
	private String	singer;				//演唱者＆演奏者
	private Integer urlType;			//资源类型:0-无额外资源／1-关联文字资源/2-关联图片资源/3-关联语音资源
    private String	url;						//资源的存储地址
    private String	summary;		//摘要
    private String	content;			//内容
    private String	photo;				//封面
    private Long		creatorId;		//创建者ID
    private Integer bizFlag;				//业务标识：0-公开／1-只对好友公开/2-不公开
    
    //查询专用
    private List<Long>  ids;
}
