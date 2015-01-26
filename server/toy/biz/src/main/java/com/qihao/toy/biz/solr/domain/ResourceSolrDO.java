package com.qihao.toy.biz.solr.domain;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class ResourceSolrDO {
	private String resourceId;
	private String title;
	private String subTitle;
	private String desc;
	private String author;
	private String composer;
	private String singer;
	private String url;
	private String summary;
	private String content;
}