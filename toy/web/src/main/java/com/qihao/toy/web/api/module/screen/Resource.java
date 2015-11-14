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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.alibaba.citrus.service.requestcontext.buffered.BufferedRequestContext;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.Navigator;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.ResourceService;
import com.qihao.toy.biz.service.UploadService;
import com.qihao.toy.biz.solr.DefaultSolrOperator;
import com.qihao.toy.biz.utils.AnnexUtils;
import com.qihao.toy.dal.domain.MyResourceDO;
import com.qihao.toy.dal.domain.ResourceDO;
import com.qihao.toy.dal.domain.UploadDO;
import com.qihao.toy.dal.persistent.MyResourceMapper;
import com.qihao.toy.web.api.base.BaseApiScreenAction;

@Slf4j
public class Resource extends BaseApiScreenAction {
	@Autowired
	private AccountService accountService;
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private MyResourceMapper myResourceMapper;
	@Autowired
	private UploadService uploadService;
	@Autowired
	private DefaultSolrOperator solrOperator;
	@Autowired
	private BufferedRequestContext buffered;

	/**
	 * 单文件上传
	 * 
	 * @param requestParams
	 * @throws IOException
	 */
	public void doUpload(ParameterParser requestParams,
			@Param("authToken") String authToken,
			@Param("myFile") FileItem myFile, @Param("fileType") String fileType)
			throws IOException {
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<Map<String, Object>> result = new DataResult<Map<String, Object>>();
		FileItem myfile = requestParams.getFileItem("myFile");

		String filename = myfile.getName();
		InputStream istream = myfile.getInputStream();

		if (null == myFile) {
			result.setSuccess(false);
			result.setErrorCode(1000);
			result.setMessage("请指定上传文件！");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}

		String fileName = AnnexUtils.saveAnnex(myFile);
		String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
		UploadDO.FileType fileTypeEnum = null;
		if (null != fileType) {
			fileTypeEnum = Enum.valueOf(UploadDO.FileType.class, fileType);
		}
		if (null == fileTypeEnum) {
			fileTypeEnum = UploadDO.FileType.SOUND;
		}
		Long id = uploadService.insert(fileName, fileSuffix, fileTypeEnum,
				currentUser.getId());
		Map<String, Object> data = Maps.newLinkedHashMap();
		data.put("id", id);
		data.put("fileName", fileName);
		data.put("fileType", fileTypeEnum.name());
		data.put("fileSuffix", fileSuffix);
		result.setSuccess(true);
		result.setMessage("文件上传成功!");
		result.setData(data);
		response.getWriter().println(JSON.toJSONString(result));
		return;
	}

	public void doGetUploadInfo(ParameterParser requestParams,
			@Param("authToken") String authToken, @Param("id") Long id,
			@Param("fileName") String fileName) throws IOException {
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<Map<String, Object>> result = new DataResult<Map<String, Object>>();

		if (null == id && null == fileName) {
			result.setSuccess(false);
			result.setErrorCode(1000);
			result.setMessage("请指定下载文件！");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		UploadDO searchDO = new UploadDO();
		searchDO.setId(id);
		searchDO.setFileName(fileName);
		List<UploadDO> resp = uploadService.getAll(searchDO);
		if (CollectionUtils.isEmpty(resp)) {
			result.setSuccess(false);
			result.setErrorCode(1000);
			result.setMessage("待下载文件不存在！");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		UploadDO uploadDO = resp.get(0);
		Map<String, Object> data = Maps.newLinkedHashMap();
		data.put("id", uploadDO.getId());
		data.put("fileName", uploadDO.getFileName());
		data.put("fileType", uploadDO.getFileType().name());
		data.put("fileSuffix", uploadDO.getFileSuffix());
		result.setSuccess(true);
		result.setMessage("文件上传成功!");
		result.setData(data);
		response.getWriter().println(JSON.toJSONString(result));
		return;
	}

	public void doDownload(@Param("authToken") String authToken,
			@Param("id") Long id, @Param("fileName") String fileName,
			Navigator nav) throws Exception {
		Assert.notNull(currentUser, "用户未登录!");
		if (null != id) {
			UploadDO uploadDO = uploadService.getById(id);
			if (null != uploadDO) {
				fileName = uploadDO.getFileName();
			}
		}
		buffered.setBuffering(false);
		try {
			OutputStream out = response.getOutputStream();
			File downloadFile = AnnexUtils.getAnnexFile(fileName);
			if (null == downloadFile) {
				out.write("Please input filename".getBytes());
				out.flush();
				out.close();
				return;
			}
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(downloadFile));
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
			response.setHeader("Content-Disposition", "attachment;filename=\""
					+ fileName + "\"");
			response.setContentLength(totalLen);

			out.close();
		} catch (Exception e) {

		}
	}

	/**
	 * 获取指定id列表资源信息
	 * 
	 * @param ids
	 * @param nav
	 * @throws Exception
	 */
	public void doGetResourceItems(@Param("ids") String ids, Navigator nav)
			throws Exception {
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<List<ResourceDO>> result = new DataResult<List<ResourceDO>>();
		Iterable<String> split = Splitter.on(',').split(ids);
		List<Long> resourceIds = Lists.newArrayList();
		for (String id : split) {
			resourceIds.add(Long.valueOf(id));
		}
		ResourceDO resource = new ResourceDO();
		resource.setIds(resourceIds);
		List<ResourceDO> resp = resourceService.getAll(resource);
		result.setSuccess(true);
		result.setMessage("成功!");
		result.setData(resp);
		response.getWriter().println(JSON.toJSONString(result));
		return;
	}

	public void doInsertMyResource(@Param("resourceId") Long resourceId,
			@Param("masterId") Long masterId, @Param("slaveId") Long slaveId,
			Navigator nav) throws Exception {
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<Long> result = new DataResult<Long>();
		ResourceDO resource = resourceService.getById(resourceId);
		if (null == resource) {
			result.setSuccess(false);
			result.setMessage("资源不存在!");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		MyResourceDO myResource = new MyResourceDO();
		myResource.setResourceId(resourceId);
		myResource.setMasterId(masterId);
		myResource.setSlaveId(slaveId);
		try {
			myResourceMapper.insert(myResource);
		} catch (Exception e) {
			log.error(e.getMessage());
			result.setSuccess(false);
			result.setMessage("数据库异常!");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		result.setSuccess(true);
		result.setMessage("成功!");
		result.setData(myResource.getId());
		response.getWriter().println(JSON.toJSONString(result));
		return;
	}

	public void doGetMyResources(@Param("page") Integer page,
			@Param("masterId") Long masterId, @Param("slaveId") Long slaveId,
			Navigator nav) throws Exception {
		Assert.notNull(currentUser, "用户未登录!");

		DataResult<List<ResourceDO>> result = new DataResult<List<ResourceDO>>();
		if (null == masterId || null == slaveId) {
			result.setSuccess(false);
			result.setMessage("参数不能为空!");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}

		MyResourceDO myResource = new MyResourceDO();
		myResource.setMasterId(masterId);
		myResource.setSlaveId(slaveId);
		
		try {
			List<MyResourceDO> resp = myResourceMapper.getAll(myResource);
			if (CollectionUtils.isEmpty(resp)) {
				result.setSuccess(true);
				result.setMessage("成功!");
				result.setData(null);
				response.getWriter().println(JSON.toJSONString(result));
				return;
			}
			List<Long> ids = Lists.newArrayList();
			for(MyResourceDO item : resp) {
				ids.add(item.getResourceId());
			}
			ResourceDO resourceDO  = new ResourceDO();
			resourceDO.setIds(ids);
			resourceDO.setPage(page<1? 1: page);
			resourceDO.setLimit(20);
			List<ResourceDO> data = resourceService.getAll(resourceDO);
			result.setSuccess(true);
			result.setMessage("成功!");
			result.setData(data);
			response.getWriter().println(JSON.toJSONString(result));
			return;
		} catch (Exception e) {
			result.setSuccess(false);
			result.setMessage("数据库异常!");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}

	}
}
