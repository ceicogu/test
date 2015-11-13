package com.ucpaas.im.util;

import java.util.List;

import com.ucpaas.im.constant.Constants;

/**
 * 请求相关工具类
 *
 */
public class RequestUtil {

	/**
	 * 签名
	 * @param accountSid
	 * @param authToken
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public static String getSignature(String accountSid, String authToken,
			String timestamp) throws Exception {
		String sig = accountSid + authToken + timestamp;
		String signature = EncryptUtil.md5Digest(sig);
		return signature;
	}
	
	/**
	 * 组装im-rest请求url
	 * @param targetUrl
	 * @param restPath
	 * @param accountSid
	 * @param authToken
	 * @param timestamp
	 * @return
	 * @throws Exception
	 */
	public static String getIMRequestUrl(String targetUrl,String restPath, String accountSid, String authToken,
			String timestamp) throws Exception {
		String signature = getSignature(accountSid, authToken, timestamp);
		StringBuffer requestUrl = new StringBuffer(Constants.HTTP_PREFIX);
		requestUrl.append(targetUrl).append("/").append(Constants.VERSION2015)
				  .append("/").append(Constants.RESTAPI).append("/")
				  .append(accountSid).append(restPath).append("?")
				  .append(Constants.SIG).append("=").append(signature);
		return requestUrl.toString();
	}
	
	/**
	 * userId集合转字符串
	 * @param userIdList
	 * @return
	 * @throws Exception
	 */
	public static String getUserIds(List<String> userIdList) throws Exception{
		String userIds = "";
		if(!userIdList.isEmpty()){
			for(String userId : userIdList){
				userIds += userId + ",";
			}
			userIds = userIds.substring(0,userIds.length()-1);
		}
		return userIds;
	}
	
	
	public static String getClientRequestUrl(String targetUrl,String restPath, String accountSid, String authToken,
			String timestamp) throws Exception {
		String signature = getSignature(accountSid, authToken, timestamp);
		StringBuffer requestUrl = new StringBuffer(Constants.HTTP_PREFIX);
		requestUrl.append(targetUrl).append("/").append(Constants.VERSION2015)
				  .append("/").append(Constants.ACCOUNTS).append("/")
				  .append(accountSid).append(restPath).append("?")
				  .append(Constants.SIG).append("=").append(signature);
		return requestUrl.toString();
	}
	
	
	
	
	
	

	
	

}
