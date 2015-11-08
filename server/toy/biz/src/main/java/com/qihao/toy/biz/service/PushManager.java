package com.qihao.toy.biz.service;

import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.UserDO;

public interface PushManager {
	public void sendNotification(UserDO.DeviceType deviceType, String deviceToken,StationLetterDO message);
}
