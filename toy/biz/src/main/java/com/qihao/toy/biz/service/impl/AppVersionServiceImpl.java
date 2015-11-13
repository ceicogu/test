package com.qihao.toy.biz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qihao.toy.biz.service.AppVersionService;
import com.qihao.toy.dal.domain.AppVersionDO;
import com.qihao.toy.dal.persistent.AppVersionMapper;

@Service
public class AppVersionServiceImpl implements AppVersionService {
	
	@Autowired
	private AppVersionMapper appVersionMapper;

	@Override
	public Long addNewVersionApp(AppVersionDO appVersion) {
		return appVersionMapper.addNewVersionApp(appVersion);
	}

	@Override
	public AppVersionDO findLatestVersionApp(int deviceType, int platformType) {
		return appVersionMapper.findLatestVersionApp(deviceType, platformType);
	}

}
