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

package com.qihao.toy.biz.solr.domain;

import lombok.Getter;
import lombok.Setter;

import org.apache.solr.common.SolrDocumentList;
/**
 * 语音识别指令库表
 * @author luqiao
 *
 */
@Setter @Getter
public class AnswerSolrDO {
	private Long  id;	
	private String  question;		//问题
    private String	type;		//识别后的语音指令类型
    private String	answer;		//对应的应答
    
    private SolrDocumentList info;  //定位后的具体内容
}
