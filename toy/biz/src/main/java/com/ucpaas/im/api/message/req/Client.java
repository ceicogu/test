package com.ucpaas.im.api.message.req;

import java.io.Serializable;

/**
 * 子账号信息
 */
public class Client implements Serializable {
	private static final long serialVersionUID = -2332163690991076512L;
	private String appId;// 开发者创建的应用ID
	private String userId;// 开发者应用下注册用户的ID
	private String friendlyName;// 开发者应用下注册用户对应的昵称
	private String mobile;// 绑定的手机号码

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

}
