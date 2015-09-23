package com.qihao.toy.biz.solr.domain;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AccountSolrDO {
    private Long id;
	private Long myId;
	private Long friendId;
	private String relation;
}