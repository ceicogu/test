package com.qihao.toy.dal.domain;

import java.util.Date;

import lombok.Data;

@Data
public class AppVersionDO {
	
	private Long id;
	
	private Integer deviceType;	//设备类型：1:android/2:ios/3:wp
	
	private Integer platformType;	//设备平台类型：1:mobile/2:tv/3:pc/4:tablet
	
	private Integer versionCode;	//版本号
	
	private String versionName;	//版本名称
	
	private boolean forcedUpdate;	//是否强制更新
	
	private String downloadUrl;	//下载地址
	
	private String description;	//描述
	
	private Date publishTime;	//发布时间
	

}
