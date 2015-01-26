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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.fastjson.JSON;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.biz.solr.domain.ResourceSolrDO;
import com.qihao.toy.biz.utils.AnnexUtils;
import com.qihao.toy.dal.domain.UserDO;

import org.apache.commons.fileupload.FileItem;
import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;

public class Resource {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private AccountService accountService;
    @Autowired
    private GlobalConfig globalConfig;
    @Autowired
    private DefaultSolrOperator solrOperator;
    
    /**
     * 单文件上传
     * @param requestParams
     * @throws IOException
     */
    public void doUpload(@Param("authToken") String authToken, @Param("myFile") FileItem myFile) throws IOException {
    	DataResult<String> result =  new DataResult<String>();
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}   
    	if(null == myFile){
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请指定上传文件！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		    		
    	}
    	String fileName = AnnexUtils.saveAnnex(myFile);
    	result.setSuccess(true);
    	result.setMessage("文件上传成功!");
    	result.setData(fileName);
        response.getWriter().println(JSON.toJSONString(result));
        return;    
    }
    
    public void doDownload(@Param("authToken") String authToken, @Param("fileName") String fileName, Navigator nav) throws Exception {
    	SimpleResult result = new SimpleResult();
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}       	
        try {
            OutputStream out = response.getOutputStream();
            File downloadFile = AnnexUtils.getAnnexFile(fileName);
            if(null == downloadFile){
                out.write("Please input filename".getBytes());
                out.flush();
                out.close();
                return;
            }            
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(downloadFile));
            byte[] buf = new byte[2048];  
            
            int length = in.read(buf);
            int totalLen = 0;
            while (length != -1) {
            	totalLen += length;
                out.write(buf, 0, length);  
                length = in.read(buf);  
            }  
            in.close();  
            response.setContentType("application/force-download");
            response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
            response.setContentLength(totalLen);

            out.close();
        } catch (Exception e) {

        }
    }
    public void doSearch(@Param("authToken") String authToken, @Param("q") String query, Navigator nav) throws Exception {
    	DataResult<List<Object>> result  = new DataResult<List<Object>>();
    	UserDO userDO = accountService.validateAuthToken(authToken);
    	if(null == userDO) {
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("请登录！");
            response.getWriter().println(JSON.toJSONString(result));
            return;    		
    	}       
    	ResourceSolrDO resourceSolrDO =  new ResourceSolrDO();
    	resourceSolrDO.setTitle(query);
    	resourceSolrDO.setContent(query);
    	ResourceSolrDO compositorDO = new ResourceSolrDO();
    	compositorDO.setTitle(SolrQuery.ORDER.desc.toString());
    	
 //   	 Long count = solrOperator.querySolrResultCount(resourceSolrDO,null);
    	 List<Object> resp = solrOperator.querySolrResult((Object)resourceSolrDO, null, null, null);
     	result.setSuccess(true);
     	result.setMessage("搜索成功!");
     	result.setData(resp);
         response.getWriter().println(JSON.toJSONString(result));
         return;    
    }
}
