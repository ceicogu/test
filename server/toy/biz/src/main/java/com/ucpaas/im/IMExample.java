package com.ucpaas.im;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucpaas.im.api.IMGroupHttpMethod;

/**
 * 群组管理rest接口示例
 */
public class IMExample {
	private static final Logger logger = LoggerFactory.getLogger(IMExample.class);
	private static final String accountSid = "4c1990a5c1ad2674bc94bc39a6fd0699";// 开发者账号ID
	private static final String authToken = "5b9f1813074da6a7799d990ee7014111";// 账户授权令牌
	private static final String appId = "9a8a3b70cc8c489cb17d0aef5d08d5f3";// 开发者创建的应用ID

	public static void main(String[] args) {
		imCreateGroup();// 创建群组
		// imDismissGroup();// 释放群组
		// imJoinGroupBatch();// 加入群组
		// imQuitGroup();// 退出群组
		// imUpdateGroup();// 更新群组
		// imGetGroup();// 查询群组
	}

	/**
	 * 创建群组
	 */
	public static void imCreateGroup() {
		IMGroupHttpMethod imGroupHttpMethod = new IMGroupHttpMethod("api.ucpaas.com");
		String userId = "1001";// 开发者应用下用户的ID
		String groupId = "1";// 群组ID
		String groupName = "旅游群";// 群组名称

		String resp = imGroupHttpMethod.imCreateGroup(accountSid, authToken, appId, userId, groupId, groupName);
		logger.debug(resp);
	}

	/**
	 * 释放群组
	 */
	public static void imDismissGroup() {
		IMGroupHttpMethod imGroupHttpMethod = new IMGroupHttpMethod("api.ucpaas.com");
		String groupId = "1";// 群组ID

		String resp = imGroupHttpMethod.imDismissGroup(accountSid, authToken, appId, groupId);
		logger.debug(resp);
	}

	/**
	 * 加入群组
	 */
	public static void imJoinGroupBatch() {
		IMGroupHttpMethod imGroupHttpMethod = new IMGroupHttpMethod("api.ucpaas.com");
		String groupId = "1";// 群组ID
		List<String> userIdList = new ArrayList<String>();
		userIdList.add("1001");// 开发者应用下用户的ID
		userIdList.add("1002");// 开发者应用下用户的ID

		String resp = imGroupHttpMethod.imJoinGroupBatch(accountSid, authToken, appId, userIdList, groupId);
		logger.debug(resp);
	}

	/**
	 * 退出群组
	 */
	public static void imQuitGroup() {
		IMGroupHttpMethod imGroupHttpMethod = new IMGroupHttpMethod("api.ucpaas.com");
		String groupId = "1";// 群组ID
		String userId = "1001";// 开发者应用下用户的ID

		String resp = imGroupHttpMethod.imQuitGroup(accountSid, authToken, appId, userId, groupId);
		logger.debug(resp);
	}

	/**
	 * 更新群组
	 */
	public static void imUpdateGroup() {
		IMGroupHttpMethod imGroupHttpMethod = new IMGroupHttpMethod("api.ucpaas.com");
		String groupId = "1";// 群组ID
		String groupName = "徒步群";// 群组名称
		String resp = imGroupHttpMethod.imUpdateGroup(accountSid, authToken, appId, groupId, groupName);
		logger.debug(resp);
	}

	/**
	 * 查询群组
	 */
	public static void imGetGroup() {
		IMGroupHttpMethod imGroupHttpMethod = new IMGroupHttpMethod("api.ucpaas.com");
		String groupId = "1";// 群组ID
		String resp = imGroupHttpMethod.imGetGroup(accountSid, authToken, appId, groupId);
		logger.debug(resp);
	}

}
