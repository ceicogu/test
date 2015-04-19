package com.qihao.toy.dal.domain;

import java.io.Serializable;

import lombok.Data;

import com.qihao.toy.dal.enums.CommandTypeEnum;
import com.qihao.toy.dal.enums.OperateTypeEnum;
@Data
public class MiPushCommandMessage implements Serializable {
	private static final long serialVersionUID = 7846379801688580643L;
	//推送消息发送者
	private Long senderId;
	//推送消息类型：0-显示指令/1-下载指令/2.助手计划指令
	private CommandTypeEnum cmdType;
	//推送消息操作类型:ADD/DELETE/UPDATE
	private OperateTypeEnum optType;
	//指令对应的指令ID(消息ID/下载资源ID/宝贝助手计划ID)
	private Long cmdId;
	//指令具体内容(针对不同的指令，里边的内容是不同的，JSON)
	private String cmdContent;
}
