package com.qihao.toy.biz.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.qihao.toy.biz.service.PushManager;
import com.qihao.toy.biz.utils.MiPushUtils;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.domain.SubscribeMessageDO;
import com.qihao.toy.dal.domain.UserDO.DeviceType;
import com.qihao.toy.dal.domain.enums.CommandTypeEnum;
import com.qihao.toy.dal.domain.enums.MiContentTypeEnum;
import com.qihao.toy.dal.persistent.SubscribeMessageMapper;
import com.xiaomi.xmpush.server.Message;

public class PushManagerImpl implements PushManager {
	@Autowired
	private SubscribeMessageMapper subscribeMessageMapper;
	
	public void sendNotification(DeviceType deviceType, String deviceToken,
			StationLetterDO message) {
		// TODO Auto-generated method stub
		if(deviceType.equals(DeviceType.Ios)) {
			sendNotificationToApplyClient(deviceToken, message);
		} else {
			sendNotificationToMiClient(deviceToken, message);
		}
	}
	private void sendNotificationToApplyClient(String deviceToken, StationLetterDO letter){
        /**APNS推送需要的证书、密码、和设备的Token**/
        String  p12Path = "D:/MbaikeDevCertificates.p12";
        String  password = "123456";
  
        try {
            /**设置参数，发送数据**/
            ApnsService service =APNS.newService().withCert(p12Path,password).withSandboxDestination().build();
            String payload = APNS.newPayload().alertBody("hello,www.mbaike.net").badge(1).sound("default").build();
            service.push(deviceToken, payload);
            System.out.println("推送信息已发送！");
        } catch (Exception e) {
            System.out.println("出错了："+e.getMessage());
        }		
	}
	private void sendNotificationToMiClient(String deviceToken, StationLetterDO letter){
		Map<Long, String> handleMap = Maps.newLinkedHashMap();
		try {
			Message message = MiPushUtils.buildMessage(
					CommandTypeEnum.CONTENT_VIEW.name(), 
					MiContentTypeEnum.LETTER.name(),
					letter);				

			SubscribeMessageDO msg = new SubscribeMessageDO();
			msg.setMessageId(letter.getId());
			msg.setUserId(letter.getSenderId());
			msg.setSenderId(letter.getSenderId());
			String miMessageId = MiPushUtils.sendMessageToAlias(message, deviceToken);
			if(null != miMessageId) {
				handleMap.put(letter.getSenderId(), miMessageId);
				msg.setStatus(1);
				msg.setMiMessageId(miMessageId);
			}else {
				msg.setStatus(0);
			}
			subscribeMessageMapper.insert(msg);
		}catch(Exception e){
			
		}
	}
}
