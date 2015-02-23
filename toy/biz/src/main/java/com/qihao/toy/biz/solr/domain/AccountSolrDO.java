package com.qihao.toy.biz.solr.domain;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AccountSolrDO {
	private Long groupId;
	private String groupName;
	private Integer groupType;
	private Long memberId;
	private String memberName;
}