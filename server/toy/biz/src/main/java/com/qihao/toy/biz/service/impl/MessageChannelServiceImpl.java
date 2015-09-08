package com.qihao.toy.biz.service.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.utils.SendSMSUtils;
import com.qihao.toy.biz.utils.VelocityUtils;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
@Slf4j
@Service
public class MessageChannelServiceImpl implements MessageChannelService {	
	@Autowired
	private GlobalConfig globalConfig;
	
	private final static  Map<VerifyCodeTypeEnum, String>  mcTpllMaps  = Maps.newTreeMap();
	static {
		mcTpllMaps.put(VerifyCodeTypeEnum.Reg_VerifyCode, "【奇好故事机】您的验证码是：${code}，请尽快注册防止失效");
		mcTpllMaps.put(VerifyCodeTypeEnum.Reg_InviteCode, "【奇好故事机】%s邀请您成为好友！邀请码是：${code}，请尽快注册防止失效");
	}
	
	public boolean sendMessage(VerifyCodeTypeEnum vmTpl, String mobile,Map<String,Object> parameter){
		if(!mcTpllMaps.containsKey(vmTpl)) {
			log.error("无匹配的短信模版!vmTpl={}",vmTpl.name());
			throw new RuntimeException("无匹配的短信模板");
		}	
		String vm = mcTpllMaps.get(vmTpl);
		String content = VelocityUtils.evaluate(vm, parameter,vmTpl.name());
		boolean bSend = SendSMSUtils.sendsms(mobile, content);
		return bSend;
	}
}
