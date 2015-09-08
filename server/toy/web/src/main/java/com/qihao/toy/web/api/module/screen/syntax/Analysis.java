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

package com.qihao.toy.web.api.module.screen.syntax;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Context;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.ResourceService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.biz.solr.domain.AccountSolrDO;
import com.qihao.toy.biz.solr.domain.AnswerSolrDO;
import com.qihao.toy.biz.solr.domain.ResourceSolrDO;
import com.qihao.toy.dal.domain.ResourceDO;
import com.qihao.toy.dal.enums.VoiceCmdTypeEnum;
import com.qihao.toy.web.api.base.BaseApiScreenAction;

/**
 * 这个例子演示了用一个screen类处理多个事件的方法。
 *
 * @author Michael Zhou
 */
@Slf4j
public class Analysis extends BaseApiScreenAction{
    @Autowired
    private AccountService accountService;
    @Autowired
    private ToyService toyService;
    @Autowired
    private ResourceService resourceService;    
    @Autowired
    private StationLetterService stationLetterService;    
    @Autowired
    private MessageChannelService messageChannelService;
    @Autowired
    private DefaultSolrOperator solrOperator;
    
  
    /**
     * 语音指令识别
     * @param requestParams
     * @throws IOException
     */
    public void execute(ParameterParser requestParams, Context context) throws Exception {
    	super.beforeExecution();
    	Assert.notNull(currentUser, "用户未登录!");
    	String query = requestParams.getString("q");
    	DataResult<AnswerSolrDO> result  = new DataResult<AnswerSolrDO>(); 
    	
    	SolrDocumentList analysisResult = this.queryVoiceAnswer(query);
    	if(CollectionUtils.isEmpty(analysisResult)){
         	result.setSuccess(false);
         	result.setMessage("无法识别");
            response.getWriter().println(JSON.toJSONString(result));
            return;      		
    	}
    	//取识别出的第一条记录，用于再分析进行精准定位
    	SolrDocumentList data= null;

    	SolrDocument solrDocument = analysisResult.get(0);
    	String answer = solrDocument.get("answer").toString();

    	Iterable<String> arr = Splitter.on("|").trimResults().split(answer);
    	//AnswerSolrDO a = (AnswerSolrDO) SolrObject.toBean(solrDocument, AnswerSolrDO.class);
    	String cmdType = solrDocument.get("type").toString();
    	if(cmdType.equals(VoiceCmdTypeEnum.PLAY.name())){//找资源
    		data = this.getPlayInfo(query, solrDocument);
    	}else if(cmdType.equals(VoiceCmdTypeEnum.CALL.name())){//找人
    		data = this.getCallInfo(query, solrDocument);
    	}else if(cmdType.equals(VoiceCmdTypeEnum.CONTROL.name())){//控制
    		data = null;
    	}else if(cmdType.equals(VoiceCmdTypeEnum.ANSWER.desc())){//回复
    		data = null;
    	}
    	AnswerSolrDO answerSolrDO = new AnswerSolrDO();   	
    	answerSolrDO.setQuestion(query);
    	answerSolrDO.setType(solrDocument.get("type").toString());
    	answerSolrDO.setAnswer(solrDocument.get("answer").toString());
    	answerSolrDO.setInfo(data);
    	
//    	List<AnswerSolrDO> rtnData = Lists.newArrayList();
//    	rtnData.add(answerSolrDO);
    	result.setSuccess(true);
    	result.setMessage("搜索成功!");
    	result.setData(answerSolrDO);
    	response.getWriter().println(JSON.toJSONString(result));
    	return;   
    }
    /**
     * 语音指令分析，识别出什么指令
     * @param question
     * @return
     */
    private SolrDocumentList queryVoiceAnswer(String question){

    	AnswerSolrDO solrDO =  new AnswerSolrDO();
    	solrDO.setQuestion(question);

    	List<String>  fields = Lists.newArrayList();
    	fields.add("id");
    	fields.add("type");
    	fields.add("answer");   	
		try {
			SolrDocumentList resp = solrOperator.querySolrResult("answer",(Object)solrDO, null, fields,null, null);
	    	if(CollectionUtils.isEmpty(resp)){//若没有找到，就随机取一首给他即可
	    		return null;
	    	}
	    	return resp;
		} catch (Exception e) {
			return null;
		}

    }

    private SolrDocumentList getCallInfo(String query, SolrDocument answerSolrDO) throws Exception{
    	Preconditions.checkArgument(answerSolrDO.get("type").equals(VoiceCmdTypeEnum.CALL.name()), "语音指令类型不符合，%s", answerSolrDO.get("type"));
    	accountService.getMyFriends(currentUser.getId());
    	//1.在自己好友中找人
    	AccountSolrDO accountSolrDO =  new AccountSolrDO();
    	accountSolrDO.setMyId(currentUser.getId());
    	accountSolrDO.setRelation(query);    	

    	List<String>  fields = Lists.newArrayList();
    	fields.add("myId");
    	fields.add("friendId");
    	fields.add("relation");
    	fields.add("status");
    	fields.add("friendMobile");
    	fields.add("loginName");
    	SolrDocumentList resp = solrOperator.querySolrResult("account",(Object)accountSolrDO, null, fields,null, null);
    	//增加同义词识别逻辑，前期简单些
    	Map<String,String> relationMap = Maps.newLinkedHashMap();
    	relationMap.put("外婆", "姥姥");
    	relationMap.put("外公", "姥爷");
    	relationMap.put("姥姥", "外婆");
    	relationMap.put("姥爷", "外公");
    	String value = null;
    	if(CollectionUtils.isEmpty(resp)) {
	    	Set<String> a1 = relationMap.keySet();
	    	for(String key : a1) {
	    		if(query.contains(key)) {
	    			value = relationMap.get(key);
	    			accountSolrDO.setRelation(query+value);
	    			resp = solrOperator.querySolrResult("account",(Object)accountSolrDO, null, fields,null, null);
	    			if(null != resp) {
	    				break;
	    			}
	    		}
	    	}	    	
	    }

        return resp;  
    }
    private SolrDocumentList getPlayInfo(String query, SolrDocument answerSolrDO) throws Exception{
    	Preconditions.checkArgument(answerSolrDO.get("type").equals(VoiceCmdTypeEnum.PLAY.name()), "语音指令类型不符合，%s", answerSolrDO.get("type"));

    	if(StringUtils.isNotBlank(answerSolrDO.get("answer").toString())){//确认范围
    		Iterable<String> itrs = Splitter.on('|').split(answerSolrDO.get("answer").toString());
    		//故事分类或专辑名｜讲者或制作者｜具体故事，如：童话｜｜丑小鸭
    		    		
    	}
    	ResourceSolrDO resourceSolrDO =  new ResourceSolrDO();
    	resourceSolrDO.setTitle(query);
    	ResourceSolrDO compositorDO = new ResourceSolrDO();
    	compositorDO.setId(SolrQuery.ORDER.desc.toString());
    	
    	List<String>  fields = Lists.newArrayList();
    	fields.add("id");
    	fields.add("url");
    	SolrDocumentList resp = solrOperator.querySolrResult("resource",(Object)resourceSolrDO, compositorDO, fields,null, null);
    	if(CollectionUtils.isEmpty(resp)){//若没有找到，就随机取一首给他即可
    		SolrDocument solrDocument = new SolrDocument();
    		ResourceDO resource = new ResourceDO();
    		resource.setBizFlag(0);
    		resource.setLimit(100);
    		List<ResourceDO> rsp = resourceService.getAll(resource);
    		if(!CollectionUtils.isEmpty(rsp)) {
        		int num = rsp.size();
        		int idx = (int)(Math.random()*num);
        		solrDocument.addField("id", rsp.get(idx).getId());
        		solrDocument.addField("url", rsp.get(idx).getUrl());
        		if(null == resp) resp = new SolrDocumentList();
        		resp.add(solrDocument);
    		}
    	}

    	return resp;
    }
}

