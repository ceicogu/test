package com.qihao.toy.biz.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qihao.toy.biz.service.ResourceService;
import com.qihao.toy.dal.domain.ResourceDO;
import com.qihao.toy.dal.domain.ResourceTagDO;
import com.qihao.toy.dal.persistent.ResourceMapper;
import com.qihao.toy.dal.persistent.ResourceTagMapper;
@Service
public class ResourceServiceImpl implements ResourceService {
	@Autowired
	private ResourceMapper resourceMapper;
	@Autowired
	private ResourceTagMapper resourceTagMapper;
	
	public Long insert(ResourceDO resource) {
		Preconditions.checkArgument(StringUtils.isNotBlank(resource.getTitle()),"资源标题不能为空");
		resourceMapper.insert(resource);
		return resource.getId();
	}

	public Boolean update(Long resourceId, ResourceDO resource) {
		resource.setId(resourceId);
		return resourceMapper.update(resource);
	}

	public Boolean deleteById(Long resouceId) {
		return resourceMapper.deleteById(resouceId);
	}

	public ResourceDO getById(Long resouceId) {		
		return resourceMapper.getById(resouceId);
	}

	public List<ResourceDO> getAll(ResourceDO resource) {
		return resourceMapper.getAll(resource);
	}

	public List<Long> getReosurceIdsByTag(Long tagId) {
		ResourceTagDO resourceTag = new ResourceTagDO();
		resourceTag.setTagId(tagId);
		List<ResourceTagDO> resp = resourceTagMapper.getAll(resourceTag);
		if(CollectionUtils.isEmpty(resp)) {
			return null;
		}
		List<Long> data = Lists.newArrayList();
		for(ResourceTagDO item : resp) {
			data.add(item.getResourceId());
		}
		return data;
	}

	public List<ResourceDO> getReosurcesByTag(Long tagId) {
		return this.getReosurcesByTag(tagId, 1, 50);
	}

	public List<ResourceDO> getReosurcesByTag(Long tagId, Integer page,
			Integer pageSize) {
		ResourceTagDO resourceTag = new ResourceTagDO();
		resourceTag.setTagId(tagId);
		resourceTag.setPage(page);
		resourceTag.setLimit(pageSize);
		List<ResourceTagDO> resp = resourceTagMapper.getAll(resourceTag);
		if(CollectionUtils.isEmpty(resp)) {
			return null;
		}
		List<Long> data = Lists.newArrayList();
		for(ResourceTagDO item : resp) {
			data.add(item.getResourceId());
		}
		ResourceDO resource = new ResourceDO();
		resource.setResourceIds(data);
		return resourceMapper.getAll(resource);		
	}

}
