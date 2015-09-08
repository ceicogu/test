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
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.AlbumService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.biz.solr.domain.AlbumSolrDO;
import com.qihao.toy.dal.domain.AlbumDO;
import com.qihao.toy.web.api.base.BaseApiScreenAction;

@Slf4j
public class Album extends BaseApiScreenAction{
    @Autowired
    private AccountService accountService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private DefaultSolrOperator solrOperator;
    /**
     * 创建专辑
     * @param requestParams
     * @throws IOException
     */
    public void doCreateAlbum(
    		@Param("title") String title, 
    		@Param("summary") String summary, 
    		@Param("photo") String photo) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Long> result = new DataResult<Long>(); 	
    	if(StringUtils.isBlank(title)) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("专辑标题不能为空！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	AlbumDO album = new AlbumDO();
    	album.setTitle(title);
    	album.setSummary(summary);
    	album.setPhoto(photo);
    	album.setCreatorId(currentUser.getId());
    	Long id = albumService.insert(album);
    	result.setSuccess(true);
    	result.setData(id);
    	response.getWriter().println(JSON.toJSONString(result));    	
    }    
    /**
     * 根据ID修改专辑
     * @param id
     * @param title
     * @param summary
     * @param photo
     * @throws IOException
     */
    public void doUpdateAlbum(
    		@Param("albumId")  Long albumId,
    		@Param("title") String title, 
    		@Param("summary") String summary, 
    		@Param("photo") String photo) throws IOException {
    	SimpleResult result = new SimpleResult(); 	
    	if(null != albumId) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指令要修改的专辑！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}    	
    	AlbumDO album = albumService.getById(albumId);
    	if(null == album) {
    		log.debug("专辑不存在！alibumId={}",albumId);
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("专辑不存在！");
            response.getWriter().println(JSON.toJSONString(result));
            return;        		
    	}
    	if(StringUtils.isBlank(title)) album.setTitle(title);
    	if(StringUtils.isBlank(summary)) album.setTitle(summary);
    	if(StringUtils.isBlank(photo)) album.setTitle(photo);
    	boolean bUpdate = albumService.update(albumId, album);
    	if(bUpdate){
    		result.setSuccess(true);    		
    	}
    	else {
    		result.setSuccess(false);
    		result.setErrorCode(300000);
    		result.setMessage("修改专辑失败!");
    	}
        response.getWriter().println(JSON.toJSONString(result));
        return;       
    }
    /**
     * 根据ID删除专辑
     * @param id
     * @param title
     * @param summary
     * @param photo
     * @throws IOException
     */
    public void doDeleteAlbum(@Param("albumId")  Long albumId) throws IOException {
    	SimpleResult result = new SimpleResult(); 	
    	if(null != albumId) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("请指令要修改的专辑！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}    	
    	AlbumDO album = albumService.getById(albumId);
    	if(null == album) {
            result.setSuccess(false);
            result.setErrorCode(2000);
            result.setMessage("专辑不存在！");
            response.getWriter().println(JSON.toJSONString(result));
            return;        		
    	}
    	boolean bDelete = albumService.deleteById(albumId);
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
    /**
     * 专辑搜索
     * @param requestParams
     * @throws IOException
     */
    public void doSearch(@Param("q") String query) throws Exception {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<SolrDocumentList> result  = new DataResult<SolrDocumentList>(); 
    	AlbumSolrDO albumSolrDO =  new AlbumSolrDO();
    	albumSolrDO.setTitle(query);    	
    	albumSolrDO.setSummary(query);

    	List<String>  fields = Lists.newArrayList();
    	fields.add("id");
    	SolrDocumentList resp = solrOperator.querySolrResult("album",(Object)albumSolrDO, null, fields,null, null);
     	result.setSuccess(true);
     	result.setMessage("搜索成功!");
     	result.setData(resp);
         response.getWriter().println(JSON.toJSONString(result));
         return;   
    }
}

