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

package com.qihao.toy.web.api.module.screen;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.fastjson.JSON;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.BabyHelperService;
import com.qihao.toy.biz.service.ToyService;
import com.qihao.toy.dal.domain.BabyHelperDO;
import com.qihao.toy.dal.domain.ToyDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.web.base.BaseApiScreenAction;

@Slf4j
public class Helper extends BaseApiScreenAction{
    @Autowired
    private AccountService accountService;
    @Autowired
    private BabyHelperService babyHelperService;
    @Autowired
    private ToyService toyService;    
    /**
     * 创建
     * @param requestParams
     * @throws IOException
     */
    public void doAdd(
    		@Param("actTime") String actTime, 
    		@Param("repeats") String repeats,
    		@Param("tags") String tags,
    		@Param("url") String url) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Long> result = new DataResult<Long>(); 	
    	if(StringUtils.isBlank(actTime)) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("时间为设置！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	//获取该操作者对应的故事机信息
    	ToyDO toy = new ToyDO();
    	toy.setOwnerId(currentUser.getId());
    	toy.setIsDeleted(0);
    	List<ToyDO> resp = toyService.getAll(toy);
    	if(CollectionUtils.isEmpty(resp)){
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("操作无效！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}
    	toy = resp.get(0);
    	BabyHelperDO bean = new BabyHelperDO();
    	bean.setOperatorId(currentUser.getId());
    	bean.setToyUserId(toy.getActivatorId());
    	bean.setActTime(actTime);
    	bean.setRepeats(repeats);
    	bean.setTags(tags);
    	bean.setUrl(url);
    	Long id = babyHelperService.insert(bean);
    	result.setSuccess(true);
    	result.setData(id);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }    
    /**
     * 根据ID修改
     * @param id
     * @param title
     * @param summary
     * @param photo
     * @throws IOException
     */
    public void doUpdate(
    		@Param("id") Long id, 
       		@Param("actTime") String actTime, 
    		@Param("repeats") String repeats,
    		@Param("tags") String tags,
    		@Param("url") String url) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Long> result = new DataResult<Long>(); 	
    	if(StringUtils.isBlank(actTime)) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("时间为设置！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	BabyHelperDO bean = new BabyHelperDO();
    	bean.setOperatorId(currentUser.getId());
    	bean.setActTime(actTime);
    	bean.setRepeats(repeats);
    	bean.setTags(tags);
    	bean.setUrl(url);
    	boolean bOK = babyHelperService.update(id,bean);
    	if(false == bOK) {
    		result.setSuccess(false);
    		result.setMessage("设置失败!");
    	}
    	else {
    		result.setSuccess(true);
    		result.setMessage("设置成功!");
    	}    	
    	response.getWriter().println(JSON.toJSONString(result));    
    }
    /**
     * 根据ID删除专辑
     * @param id
     * @param title
     * @param summary
     * @param photo
     * @throws IOException
     */
    public void doDelete(@Param("id")  Long id) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	SimpleResult result = new SimpleResult(); 	
    	if(null != id) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指令要修改ID！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}    	
    	BabyHelperDO bean = babyHelperService.getById(id);
    	if(null == bean || !bean.getOperatorId().equals(currentUser.getId())) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("不存在！");
            response.getWriter().println(JSON.toJSONString(result));
            return;        		
    	}
    	boolean bDelete = babyHelperService.deleteById(id);
    	if(bDelete){
    		result.setSuccess(true);    		
    	}
    	else {
    		result.setSuccess(false);
    		result.setErrorCode(300000);
    		result.setMessage("删除专辑失败!");
    	}
        response.getWriter().println(JSON.toJSONString(result));
        return;     
    }
    public void doGetAll(
    		@Param("type") int type, 
    		@Param("toyUserId") long toyUserId) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<List<BabyHelperDO>> result = new DataResult<List<BabyHelperDO>>();
    	BabyHelperDO bean = new BabyHelperDO();
    	bean.setToyUserId(toyUserId);
    	if(type==UserDO.AccountType.Toy.intValue()) {
    		if(toyUserId!=currentUser.getId()){
    			log.debug("无匹配计划");
    			result.setSuccess(false);
    			result.setMessage("无匹配计划!");
    	        response.getWriter().println(JSON.toJSONString(result));
    	        return;     
    		}    		
    	}
    	else {
    		bean.setOperatorId(currentUser.getId());
    	}
    	List<BabyHelperDO> resp = babyHelperService.getAll(bean);
    	result.setSuccess(true);
    	result.setData(resp);
        response.getWriter().println(JSON.toJSONString(result));
        return;     
    }
}

