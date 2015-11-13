package com.ucpaas.im.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucpaas.im.api.executor.AbstractHttpMethodExecutor;
import com.ucpaas.im.api.message.req.Client;
import com.ucpaas.im.api.message.rsp.Resp;
import com.ucpaas.im.constant.Constants;
import com.ucpaas.im.util.DateUtil;
import com.ucpaas.im.util.EncryptUtil;
import com.ucpaas.im.util.JsonUtil;
import com.ucpaas.im.util.RequestUtil;

/**
 * client账号api接口
 */
public class RestClientHttpMethod extends AbstractHttpMethodExecutor {
	private Logger log = LoggerFactory.getLogger(RestClientHttpMethod.class);
	
	private static final String REGISTER_CLIENT = "/Clients";
	private static final String GET_CLIENT_BY_USERID = "/ClientsByUserId";
	private static final String GET_CLIENT_BY_MOBILE = "/ClientsByMobile";
	private static final String DROP_CLIENT = "/dropClient";


	public RestClientHttpMethod(String targetEndpoint) {
		super(targetEndpoint);
	}


	/**
	 * 注册client
	 * @param accountSid
	 * @param authToken
	 * @param client
	 * @return
	 */
	public String registerClient(String accountSid,String authToken, Client client){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getClientRequestUrl(this.targetUrl, REGISTER_CLIENT, accountSid, authToken, timestamp);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.CLIENT, client);
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("注册client失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 关闭client
	 * @param accountSid
	 * @param authToken
	 * @param client
	 * @return
	 */
	public String dropClient(String accountSid,String authToken, Client client){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getClientRequestUrl(this.targetUrl, DROP_CLIENT, accountSid, authToken, timestamp);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.CLIENT, client);
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("注册client失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 根据userId查询client信息
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param userId
	 * @return
	 */
	public String getClientByUserId(String accountSid,String authToken, String appId, String userId){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getClientRequestUrl(this.targetUrl, GET_CLIENT_BY_USERID, accountSid, authToken, timestamp);
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			String content = "&appId="+appId + "&userId="+userId;
	        return get(Constants.JSON, authorization, requestUrl.toString(), content);
		} catch (Exception e) {
			this.log.error("根据userId查询client失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * gen根据手机号查询client信息
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param mobile
	 * @return
	 */
	public String getClientByMobile(String accountSid,String authToken, String appId, String mobile){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getClientRequestUrl(this.targetUrl, GET_CLIENT_BY_MOBILE, accountSid, authToken, timestamp);
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			String content = "&appId="+appId + "&mobile="+mobile;
	        return get(Constants.JSON, authorization, requestUrl.toString(), content);
		} catch (Exception e) {
			this.log.error("根据mobile查询client失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	
}
