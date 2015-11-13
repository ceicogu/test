package com.ucpaas.im;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucpaas.im.api.RestClientHttpMethod;
import com.ucpaas.im.api.message.req.Client;

/**
 * 子账号管理rest接口示例
 */
public class ClientExample {
	private static final Logger logger = LoggerFactory.getLogger(ClientExample.class);
	private static final String accountSid = "fadc29383cca8623ece356593355c9b6";// 开发者账号ID
	private static final String authToken = "a13d40048981bd4980139328ed72885c";// 账户授权令牌
	private static final String appId = "16a4f8c3ceb44f14a228cc20ad5a7b3f";// 开发者创建的应用ID

	public static void main(String[] args) {
//		registerClient();// 创建子账号
//		 dropClient();// 释放子账号
//		 getClientByMobile();// 根据手机号查询子账号
		 getClientByUserId();// 根据userId查询子账号
	}

	/**
	 * 创建子账号
	 */
	public static void registerClient() {
		RestClientHttpMethod clientHttpMethod = new RestClientHttpMethod("api.ucpaas.com");
		Client client = new Client();
		client.setAppId(appId);// 开发者创建的应用ID
		client.setUserId("79");// 开发者应用下注册用户的ID
		client.setFriendlyName("wanli");// 开发者应用下注册用户对应的昵称
		client.setMobile("13480649573");// 绑定的手机号码

		String resp = clientHttpMethod.registerClient(accountSid, authToken, client);
		System.out.println(resp);
		logger.debug(resp);
	}

	/**
	 * 释放子账号
	 */
	public static void dropClient() {
		RestClientHttpMethod clientHttpMethod = new RestClientHttpMethod("api.ucpaas.com");
		Client client = new Client();
		client.setAppId(appId);// 开发者创建的应用ID
		client.setUserId("69982032558803");// 开发者应用下注册用户的ID

		String resp = clientHttpMethod.dropClient(accountSid, authToken, client);
		System.out.println(resp);
		logger.debug(resp);
	}

	/**
	 * 根据手机号查询子账号
	 */
	public static void getClientByMobile() {
		RestClientHttpMethod clientHttpMethod = new RestClientHttpMethod("api.ucpaas.com");
		String mobile = "18664652486";// 绑定的手机号码

		String resp = clientHttpMethod.getClientByMobile(accountSid, authToken, appId, mobile);
		System.out.println(resp);
		logger.debug(resp);
	}

	/**
	 * 根据userId查询子账号
	 */
	public static void getClientByUserId() {
		RestClientHttpMethod clientHttpMethod = new RestClientHttpMethod("api.ucpaas.com");
		String userId = "70";// 开发者应用下注册用户的ID

		String resp = clientHttpMethod.getClientByUserId(accountSid, authToken, appId, userId);
		System.out.println(resp);
		logger.debug(resp);
	}

}
