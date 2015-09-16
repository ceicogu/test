package com.qihao.toy.biz.service.impl;

import com.google.common.collect.Maps;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.GroupService;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.biz.utils.MiPushUtils;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.SubscribeMessageDO;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.enums.CommandTypeEnum;
import com.qihao.toy.dal.domain.enums.MiContentTypeEnum;
import com.qihao.toy.dal.persistent.StationLetterMapper;
import com.qihao.toy.dal.persistent.SubscribeMessageMapper;
import com.xiaomi.xmpush.server.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class StationLetterServiceImpl implements StationLetterService {
	@Autowired
	private StationLetterMapper stationLetterMapper;
	@Autowired
	private SubscribeMessageMapper subscribeMessageMapper;
	@Autowired
	private GroupService groupService;
	@Autowired
	private AccountService accountService;

	public DataResult<Long> createLetter(StationLetterDO letter) {
		DataResult<Long> result = new DataResult<Long>();
		//对接收人进行校验，只能是自己的好友或自己所在的群发送消息
		//1.若接收者是个人的话，判断是否在其好友列表		
		//2.若接收者是群的话，判断该群是senderId所在的群吗？
		Integer acceptorType = letter.getAcceptorType();
		Long		 acceptorId		=	letter.getAcceptorId();
		Long		senderId			=	letter.getSenderId();
		
		if(acceptorType.equals(0)){//我的好友
			if(!accountService.isMyFriend(senderId, acceptorId)) {//不是好友
				result.setSuccess(false);
				result.setMessage("不能给不是好友者发送消息!");
				return result;				
			}
		}else if(acceptorType.equals(1)) {//群			
			if(!groupService.isGroupMember(acceptorId, senderId)){
				result.setSuccess(false);
				result.setMessage("不能给非自己所在群发送消息!");
				return result;								
			}
		}		
		stationLetterMapper.insert(letter);
		
		//调用miPush推送消息接口，将消息ID推送给acceptorType的每一个人
		Map<Long, String> handleMap = Maps.newLinkedHashMap();
		try {
			Message message = MiPushUtils.buildMessage(
					CommandTypeEnum.CONTENT_VIEW.name(), 
					MiContentTypeEnum.LETTER.name(),
					letter);				
			if(acceptorType.equals(0)){//直接发送给好友
				UserDO user = accountService.getUser(letter.getAcceptorId());
				if(null != user) {
					SubscribeMessageDO msg = new SubscribeMessageDO();
					msg.setMessageId(letter.getId());
					msg.setUserId(user.getId());
					msg.setSenderId(letter.getSenderId());
					String miMessageId = MiPushUtils.sendMessageToAlias(message, user.getDeviceToken());
					if(null != miMessageId) {
						handleMap.put(user.getId(), miMessageId);
						msg.setStatus(1);
						msg.setMiMessageId(miMessageId);
					}else {
						msg.setStatus(0);
					}
					subscribeMessageMapper.insert(msg);
				}
			}else {
				List<Long>  userIds = groupService.getUserIdsByGroupId(letter.getAcceptorId());					
				if(!CollectionUtils.isEmpty(userIds)){
					userIds.remove(letter.getSenderId());
					List<UserDO> resp = accountService.getUserList(userIds);
					for(UserDO user : resp){						
						if(user.getId().equals(letter.getSenderId())) continue;
						//分接收人记录不同消息
						SubscribeMessageDO msg = new SubscribeMessageDO();
						msg.setMessageId(letter.getId());
						msg.setUserId(user.getId());
						msg.setSenderId(letter.getSenderId());	
						if(null == user.getDeviceToken()) {
							msg.setStatus(0);
							subscribeMessageMapper.insert(msg);
							continue;
						}

						String miMessageId = 	MiPushUtils.sendMessageToAlias(message, user.getDeviceToken());
						if(null != miMessageId) {
							handleMap.put(user.getId(), miMessageId);
							msg.setStatus(1);			
							msg.setMiMessageId(miMessageId);
						}else {
							msg.setStatus(0);								
						}
						subscribeMessageMapper.insert(msg);
					}
				}
			}
			if(CollectionUtils.isEmpty(handleMap)) {
				log.error("推送消息失败！message={},letter={}",message,letter);
			}
			else {
				log.debug("推送消息！message={},handleMap={}",message,handleMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		result.setSuccess(true);
		result.setData(letter.getId());
		return result;
	}
	public DataResult<Long> createLetter(long senderId, int acceptorType, long acceptorId,	StationLetterDO.MediaType type, String content, String url) {
		DataResult<Long> result = new DataResult<Long>();
		if(StringUtils.isBlank(content) && StringUtils.isBlank(url)) {
			log.warn("消息内容为空!");
			result.setSuccess(false);
			result.setMessage("消息内容不能为空!");
			return result;
		}

		StationLetterDO stationLetter = new StationLetterDO();
		stationLetter.setSenderId(senderId);
		stationLetter.setAcceptorType(acceptorType);
		stationLetter.setAcceptorId(acceptorId);
		stationLetter.setType(type);
		stationLetter.setContent(content);
		stationLetter.setUrl(url);
		return this.createLetter(stationLetter);
	}

	public DataResult<List<StationLetterDO>> getMyLetters( int acceptorType, long acceptorId, Integer page , Integer maxPageSize) {
		DataResult<List<StationLetterDO>> result = new DataResult<List<StationLetterDO>>();
		if(null == page || page<1) page=1;
		if(null == maxPageSize || maxPageSize<1) maxPageSize=5;
		
		StationLetterDO letter = new StationLetterDO();
		List<StationLetterDO> data = null;
		letter.setAcceptorType(acceptorType);
		letter.setAcceptorId(acceptorId);		
		letter.setLimit(maxPageSize);
		letter.setOffset((page-1)*maxPageSize);
		
		data = stationLetterMapper.getAll(letter);
	
		result.setSuccess(true);
		result.setData(data);
		return result;
	}
	public DataResult<List<StationLetterDO>> getMyLetters( long o2oId1, long o2oId2, Integer page , Integer maxPageSize){
		DataResult<List<StationLetterDO>> result = new DataResult<List<StationLetterDO>>();
		if(null == page || page<1) page=1;
		if(null == maxPageSize || maxPageSize<1) maxPageSize=5;
		
		Map<String, String> map  = Maps.newTreeMap();
		map.put("senderId", String.valueOf(o2oId1));
		map.put("acceptorId", String.valueOf(o2oId2));
		map.put("limit",maxPageSize.toString());
		map.put("offset", String.valueOf((page-1)*maxPageSize));
		
		List<StationLetterDO> data = stationLetterMapper.getAllForO2O(map);
	
		result.setSuccess(true);
		result.setData(data);
		return result;
		
	}
	public DataResult<StationLetterDO> getMyLetter(long letterId) {
		DataResult<StationLetterDO> result = new DataResult<StationLetterDO>();
		StationLetterDO data = stationLetterMapper.getById(letterId);	
		result.setSuccess(true);
		result.setData(data);
		return result;
	}
}
