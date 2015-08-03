package com.qihao.toy.biz.solr.domain;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AccountSolrDO {
	private Long myId;
	private Long friendId;
	private Integer status;
	private String relation;
	private String loginName;
	private String nickName;
	private String friendMobile;
}