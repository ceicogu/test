package com.qihao.toy.dal.persistent;

import org.apache.ibatis.annotations.Param;

import com.qihao.toy.dal.domain.AppVersionDO;

public interface AppVersionMapper {

	public Long addNewVersionApp(AppVersionDO appVersion);

	public AppVersionDO findLatestVersionApp(@Param("deviceType") int deviceType,
			@Param("deviceType") int platformType);

}
