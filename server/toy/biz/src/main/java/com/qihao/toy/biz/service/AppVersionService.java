package com.qihao.toy.biz.service;

import com.qihao.toy.dal.domain.AppVersionDO;

public interface AppVersionService {
	
	public Long addNewVersionApp(AppVersionDO appVersion);
	
	public AppVersionDO findLatestVersionApp(int deviceType, int platformType);

}
