package com.ucpaas.im.api.message.req;

import java.io.Serializable;

/**
 * 群组信息
 */
public class IMGroup implements Serializable {
	private static final long serialVersionUID = 4962054978761768821L;
	private String appId;// 开发者创建的应用ID
	private String userId;// 开发者应用下用户的ID
	private String groupId;// 群组ID
	private String groupName;// 群组名称

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

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
