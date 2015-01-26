package com.qihao.toy.biz.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qihao.toy.biz.service.AlbumService;
import com.qihao.toy.dal.domain.AlbumDO;
import com.qihao.toy.dal.domain.AlbumResourceDO;
import com.qihao.toy.dal.domain.AlbumTagDO;
import com.qihao.toy.dal.domain.ResourceDO;
import com.qihao.toy.dal.persistent.AlbumMapper;
import com.qihao.toy.dal.persistent.AlbumResourceMapper;
import com.qihao.toy.dal.persistent.AlbumTagMapper;
import com.qihao.toy.dal.persistent.ResourceMapper;
@Service
public class AlbumServiceImpl implements AlbumService {
	@Autowired
	private AlbumMapper albumMapper;
	@Autowired
	private AlbumTagMapper albumTagMapper;
	@Autowired
	private AlbumResourceMapper albumResourceMapper;
	@Autowired
	private ResourceMapper resourceMapper;
	
	public Long insert(AlbumDO album) {
		Preconditions.checkArgument(StringUtils.isNotBlank(album.getTitle()),"专辑标题不能为空");
		return albumMapper.insert(album) ;
	}

	public Boolean update(Long albumId, AlbumDO album) {
		album.setId(albumId);
		return albumMapper.update(album);
	}

	public Boolean deleteById(Long albumId) {
		return albumMapper.deleteById(albumId);
	}

	public AlbumDO getById(Long albumId) {
		return albumMapper.getById(albumId);
	}

	public List<AlbumDO> getAll(AlbumDO album) {
		return albumMapper.getAll(album);
	}

	public List<Long> getAlbumIdsByTag(Long albumId) {
		AlbumTagDO albumTag = new AlbumTagDO();
		albumTag.setTagId(albumId);
		List<AlbumTagDO> resp = albumTagMapper.getAll(albumTag);
		if(CollectionUtils.isEmpty(resp)) {
			return null;
		}
		List<Long> data = Lists.newArrayList();
		for(AlbumTagDO item : resp) {
			data.add(item.getAlbumId());
		}
		return data;
	}

	public List<AlbumDO> getAlbumsByTag(Long tagId) {
		return this.getAlbumsByTag(tagId, 1, 50);
	}

	public List<AlbumDO> getAlbumsByTag(Long tagId, Integer page,
			Integer pageSize) {
		AlbumTagDO albumTag = new AlbumTagDO();
		albumTag.setTagId(tagId);
		List<AlbumTagDO> resp = albumTagMapper.getAll(albumTag);
		if(CollectionUtils.isEmpty(resp)) {
			return null;
		}
		List<Long> data = Lists.newArrayList();
		for(AlbumTagDO item : resp) {
			data.add(item.getAlbumId());
		}
		AlbumDO album = new AlbumDO();
		album.setAlbumIds(data);
		album.setPage(page);
		album.setLimit(pageSize);
		return albumMapper.getAll(album);		
	}

	public List<ResourceDO> getResourcesByAlbumId(Long albumId) {
		AlbumResourceDO albumResource = new AlbumResourceDO();
		albumResource.setAlbumId(albumId);
		List<AlbumResourceDO> resp = albumResourceMapper.getAll(albumResource);
		if(CollectionUtils.isEmpty(resp)){
			return null;
		}
		List<Long> data = Lists.newArrayList();
		for(AlbumResourceDO item : resp) {
			data.add(item.getResourceId());
		}
		ResourceDO resource = new ResourceDO();
		resource.setResourceIds(data);
		return resourceMapper.getAll(resource);
	}

}
