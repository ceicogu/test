package com.qihao.toy.biz.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.qihao.shared.base.api.internal.util.WebUtils;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
@Slf4j
@Service
public class MessageChannelServiceImpl implements MessageChannelService {	
	@Autowired
	private GlobalConfig globalConfig;
	
	private final static  Map<VerifyCodeTypeEnum, String>  mcTpllMaps  = Maps.newTreeMap();
	static {
		mcTpllMaps.put(VerifyCodeTypeEnum.Reg_VerifyCode, "您的验证码是：%s，请尽快注册防止失效【奇好故事机】");
		mcTpllMaps.put(VerifyCodeTypeEnum.Reg_InviteCode, "%s邀请您成为好友！邀请码是：%s，请尽快注册防止失效【奇好故事机】");
	}
	
	public String getMessageTemplate(VerifyCodeTypeEnum messageType) {
		if(null == mcTpllMaps || !mcTpllMaps.containsKey(messageType)) {
			throw new RuntimeException("无匹配的短信模板");
		}		
		return mcTpllMaps.get(messageType);
	}

	public boolean sendMessage(String mobile, String content) {
		TreeMap<String,String> param = Maps.newTreeMap();
		param.put("action", "send");
		//param.put("userid", userid);
		param.put("account", globalConfig.getMessageChannelAccount());
		param.put("password",globalConfig.getMessageChannelPassword());
		param.put("mobile", mobile);
		param.put("content",content);
		param.put("sendTime",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		
		try {
			String resp = WebUtils.doGet(globalConfig.getMessageChannelURL(), param);
			log.debug(resp);
			return true;
		} catch (IOException e) {
			log.error("发送消息异常！exception={}",e);
			return false;
		}
	}
}
