package com.qihao.toy.biz.service;

import java.util.Map;

import com.qihao.toy.dal.domain.VerifyCodeDO;

/**
 * 短信消息管理
 * @author luqiao
 *
 */
public interface MessageChannelService {
	/**
	 * 向指定手机发送具体消息
	 * @param mobile
	 * @param content
	 * @return
	 */
	boolean sendMessage(VerifyCodeDO.VerifyCodeType vmTpl, String mobile,Map<String,Object> parameter);
}
