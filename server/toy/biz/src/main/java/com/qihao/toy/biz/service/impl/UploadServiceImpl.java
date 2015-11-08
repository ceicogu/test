package com.qihao.toy.biz.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.qihao.toy.biz.service.UploadService;
import com.qihao.toy.dal.domain.UploadDO;
import com.qihao.toy.dal.persistent.UploadMapper;

@Service
public class UploadServiceImpl implements UploadService {
	@Autowired 
	private UploadMapper uploadMapper;
	
	public Long insert(String fileName, String fileSuffix, UploadDO.FileType fileType,Integer duration, Long uploader) {
		Preconditions.checkArgument(StringUtils.isNotBlank(fileName),"文件名不能为空");
		UploadDO uploadDO = new UploadDO();
		uploadDO.setFileName(fileName);
		uploadDO.setFileSuffix(fileSuffix);
		uploadDO.setFileType(fileType);
		uploadDO.setDuration(duration);
		uploadDO.setUploader(uploader);
		uploadMapper.insert(uploadDO);
		return uploadDO.getId();
	}

	public Boolean update(Long id, UploadDO uploadDO) {
		uploadDO.setId(id);
		return uploadMapper.update(uploadDO);
	}

	public Boolean deleteById(Long id) {
		return uploadMapper.deleteById(id);
	}

	public UploadDO getById(Long tagId) {
		return uploadMapper.getById(tagId);
	}

	public List<UploadDO> getAll(UploadDO tag) {
		return uploadMapper.getAll(tag);
	}

}
