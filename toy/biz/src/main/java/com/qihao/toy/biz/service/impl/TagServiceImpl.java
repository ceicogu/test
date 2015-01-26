package com.qihao.toy.biz.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.qihao.toy.biz.service.TagService;
import com.qihao.toy.dal.domain.TagDO;
import com.qihao.toy.dal.persistent.TagMapper;

@Service
public class TagServiceImpl implements TagService {
	@Autowired 
	private TagMapper tagMapper;
	
	public Long insert(int tagType, String tagName) {
		return this.insert(tagType, tagName, null);
	}

	public Long insert(int tagType, String tagName, Long tagParentId) {
		Preconditions.checkArgument(StringUtils.isNotBlank(tagName),"标签名不能为空");
		TagDO tag = new TagDO();
		tag.setTagType(tagType);
		tag.setTagName(tagName);
		tag.setTagParentId(tagParentId);
		tagMapper.insert(tag);		
		return tag.getId();
	}

	public Boolean update(Long tagId, TagDO tag) {
		tag.setId(tagId);
		return tagMapper.update(tag);
	}

	public Boolean deleteById(Long tagId) {
		return tagMapper.deleteById(tagId);
	}

	public TagDO getById(Long tagId) {
		return tagMapper.getById(tagId);
	}

	public List<TagDO> getAll(TagDO tag) {
		return tagMapper.getAll(tag);
	}

}
