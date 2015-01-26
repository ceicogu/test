package com.qihao.toy.biz.config;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class GlobalConfig {
	//签名及加解密密钥
	private String md5Key;
	//AES加解密
	private String aesKey;
	private String aesIv;
	//短信消息通道配置
	private String	messageChannelURL;
	private String	messageChannelAccount;
	private String	messageChannelPassword;
}
