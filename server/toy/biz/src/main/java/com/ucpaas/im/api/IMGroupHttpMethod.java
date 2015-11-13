package com.ucpaas.im.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucpaas.im.api.executor.AbstractHttpMethodExecutor;
import com.ucpaas.im.api.message.req.IMGroup;
import com.ucpaas.im.api.message.rsp.Resp;
import com.ucpaas.im.constant.Constants;
import com.ucpaas.im.util.DateUtil;
import com.ucpaas.im.util.EncryptUtil;
import com.ucpaas.im.util.JsonUtil;
import com.ucpaas.im.util.RequestUtil;

/**
 * im群组api接口
 */
public class IMGroupHttpMethod extends AbstractHttpMethodExecutor {
	private Logger log = LoggerFactory.getLogger(IMGroupHttpMethod.class);
	
	private static final String CREATE_GROUP = "/im/group/createGroup";
	private static final String DISMISS_GROUP = "/im/group/dismissGroup";
	private static final String JOIN_GROUP_BATCH = "/im/group/joinGroupBatch";
	private static final String QUIT_GROUP = "/im/group/quitGroup";
	private static final String UPDATE_GROUP = "/im/group/updateGroup";
	private static final String GET_GROUP = "/im/group/getGroup";


	public IMGroupHttpMethod(String targetEndpoint) {
		super(targetEndpoint);
	}

	
	/**
	 * 创建群组
	 * @param accountSid	开发者账号id
	 * @param authToken		开发者token
	 * @param appId			应用id
	 * @param userId		用户id
	 * @param groupId		群组id
	 * @param groupName		群组名称
	 * @return
	 */
	public String imCreateGroup(String accountSid,String authToken,String appId, String userId, String groupId, String groupName){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
        	//获取请求url
			String requestUrl=RequestUtil.getIMRequestUrl(this.targetUrl, CREATE_GROUP, accountSid, authToken, timestamp);
			//实例化IMGroup对象并赋值 
			IMGroup group = new IMGroup();
			group.setAppId(appId);
	        group.setGroupId(groupId);
	        group.setGroupName(groupName);
	        group.setUserId(userId);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.IMGROUP, group);
	        
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("创建群组失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 释放群组
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param groupId
	 * @return
	 */
	public String imDismissGroup(String accountSid,String authToken, String appId, String groupId){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getIMRequestUrl(this.targetUrl, DISMISS_GROUP, accountSid, authToken, timestamp);
			IMGroup group = new IMGroup();
			group.setAppId(appId);
	        group.setGroupId(groupId);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.IMGROUP, group);
	        
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("释放群组失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 批量加入群组
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param userIdList
	 * @param groupId
	 * @return
	 */
	public String imJoinGroupBatch(String accountSid,String authToken, String appId, List<String> userIdList, String groupId){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getIMRequestUrl(this.targetUrl, JOIN_GROUP_BATCH, accountSid, authToken, timestamp);
			IMGroup group = new IMGroup();
			group.setAppId(appId);
	        group.setGroupId(groupId);
	        group.setUserId(RequestUtil.getUserIds(userIdList));
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.IMGROUP, group);
	        
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("批量加入群组失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 退出群组
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param userId
	 * @param groupId
	 * @return
	 */
	public String imQuitGroup(String accountSid,String authToken, String appId, String userId, String groupId){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getIMRequestUrl(this.targetUrl, QUIT_GROUP, accountSid, authToken, timestamp);
			IMGroup group = new IMGroup();
			group.setAppId(appId);
	        group.setGroupId(groupId);
	        group.setUserId(userId);
	        group.setUserId(userId);
	        
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.IMGROUP, group);
	        
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("退出群组失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 更新群组
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param groupId
	 * @param groupName
	 * @return
	 */
	public String imUpdateGroup(String accountSid,String authToken, String appId, String groupId, String groupName){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getIMRequestUrl(this.targetUrl, UPDATE_GROUP, accountSid, authToken, timestamp);
			IMGroup group = new IMGroup();
			group.setAppId(appId);
	        group.setGroupId(groupId);
	        group.setGroupName(groupName);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.IMGROUP, group);
	        
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("更新群组失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	/**
	 * 查询群组
	 * @param accountSid
	 * @param authToken
	 * @param appId
	 * @param groupId
	 * @return
	 */
	public String imGetGroup(String accountSid,String authToken, String appId, String groupId){
        try {
        	String timestamp = DateUtil.format(DateUtil.DATE_TIME_NO_SLASH, new Date());
			String requestUrl=RequestUtil.getIMRequestUrl(this.targetUrl, GET_GROUP, accountSid, authToken, timestamp);
			IMGroup group = new IMGroup();
			group.setAppId(appId);
	        group.setGroupId(groupId);
	        Map<String,Object> map = new HashMap<String,Object>();
	        map.put(Constants.IMGROUP, group);
	        
			String authorization = EncryptUtil.base64Encoder(accountSid + Constants.SEPARATOR + timestamp);
			
	        return post(Constants.JSON, authorization,requestUrl.toString(),JsonUtil.toJson(map));
		} catch (Exception e) {
			this.log.error("查询群组失败",e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		}
	}
	
	
	
	
	
}
