package com.qihao.toy.dal.domain;

import java.io.Serializable;

import lombok.Data;

import com.qihao.toy.dal.enums.MediaTypeEnum;
import com.qihao.toy.dal.enums.MiCommandTypeEnum;
@Data
public class MiPushCommandMessage implements Serializable {
	private static final long serialVersionUID = 7846379801688580643L;
	//推送消息发送者
	private Long senderId;
	//推送消息类型：0-显示指令/1-操作指令
	private MiCommandTypeEnum type;
	//媒体介质类型
	private MediaTypeEnum mediaType;
	//内部消息ID
	private Long msgId;
	//指令具体内容
	private String msgContent;
	//指令需要下载的内容
	private String msgUrl;
}
