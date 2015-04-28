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
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.ansj.domain.Nature;
import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Context;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.ResourceService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.biz.solr.domain.AccountSolrDO;
import com.qihao.toy.biz.solr.domain.ResourceSolrDO;
import com.qihao.toy.dal.domain.ResourceDO;
import com.qihao.toy.dal.enums.GroupTypeEnum;
import com.qihao.toy.web.base.BaseApiScreenAction;

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
    private GroupService groupService;
    @Autowired
    private ResourceService resourceService;    
    @Autowired
    private StationLetterService stationLetterService;    
    @Autowired
    private MessageChannelService messageChannelService;
    @Autowired
    private DefaultSolrOperator solrOperator;
    
  
    /**
     * 语音处理
     * @param requestParams
     * @throws IOException
     */
    public void execute(ParameterParser requestParams, Context context) throws Exception {
    	super.beforeExecution();
    	Assert.notNull(currentUser, "用户未登录!");
    	String query = requestParams.getString("q");
    	DataResult<List<Object>> result  = new DataResult<List<Object>>(); 
    	//1.关键词识别（听--故事）    	
    	List<Term> terms = ToAnalysis.parse(query);
    	new NatureRecognition(terms).recognition();
    	
    	List<String>  vLists = Lists.newArrayList();
    	for(Term tm : terms) {
    		if(tm.getNatureStr().equals("v")){//v-动词
    			vLists.add(tm.getName());
    		}
    	}
    	ImmutableList<String> of = ImmutableList.of("听", "想听");  
    	if(!CollectionUtils.isEmpty(vLists)) {//求交集
    		vLists.retainAll(of);
    	}
    	if(!CollectionUtils.isEmpty(vLists)) {//听故事
        	ResourceSolrDO resourceSolrDO =  new ResourceSolrDO();
        	resourceSolrDO.setTitle(query);
        	//resourceSolrDO.setContent(query);
        	ResourceSolrDO compositorDO = new ResourceSolrDO();
        	String aa = SolrQuery.ORDER.desc.toString();
        	compositorDO.setId(SolrQuery.ORDER.desc.toString());
        	
        	List<String>  fields = Lists.newArrayList();
        	fields.add("id");
        	fields.add("url");
        	List<Object> resp = solrOperator.querySolrResult("resource",(Object)resourceSolrDO, compositorDO, fields,null, null);
        	if(CollectionUtils.isEmpty(resp)){//若没有找到，就随机取一首给他即可
        		Map<String, Object> data = Maps.newConcurrentMap();
        		ResourceDO resource = new ResourceDO();
        		resource.setBizFlag(0);
        		resource.setLimit(100);
        		List<ResourceDO> rsp = resourceService.getAll(resource);
        		if(!CollectionUtils.isEmpty(rsp)) {
	        		int num = rsp.size();
	        		int idx = (int)(Math.random()*num);
	        		data.put("id", rsp.get(idx).getId());
	        		data.put("url", rsp.get(idx).getUrl());
	        		resp = Lists.newArrayList();
	        		resp.add(data);
        		}
        	}
         	result.setSuccess(true);
         	result.setMessage("搜索成功!");
         	result.setData(resp);
             response.getWriter().println(JSON.toJSONString(result));
             return;    
    	}
    	else {//找人
        	//1.确认自己所在家庭群
        	List<Long> groupIds = groupService.getMyJoinedGroups(currentUser.getId(), GroupTypeEnum.Family.numberValue());
        	//2.在自己所在家庭群找人
        	AccountSolrDO accountSolrDO =  new AccountSolrDO();
        	accountSolrDO.setMemberName(query);    	
        	if(!CollectionUtils.isEmpty(groupIds)){
        		accountSolrDO.setGroupId(groupIds.get(0));
        	}

        	List<String>  fields = Lists.newArrayList();
        	fields.add("memberId");
        	fields.add("memberName");
        	fields.add("memberMobile");
        	List<Object> resp = solrOperator.querySolrResult("account",(Object)accountSolrDO, null, fields,null, null);
         	result.setSuccess(true);
         	result.setMessage("搜索成功!");
         	result.setData(resp);
	         response.getWriter().println(JSON.toJSONString(result));
	         return;   
    	}

    }
}

