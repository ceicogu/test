package com.qihao.toy.biz.service;

import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;

/**
 * 短信消息管理
 * @author luqiao
 *
 */
public interface MessageChannelService {
	/**
	 * 根据消息类型获取对应消息模板
	 * @param messageType
	 * @return
	 */
	String getMessageTemplate(VerifyCodeTypeEnum messageType);
	/**
	 * 向指定手机发送具体消息
	 * @param mobile
	 * @param content
	 * @return
	 */
	boolean sendMessage(String mobile, String content);
}
