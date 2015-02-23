package com.qihao.toy.biz.solr;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
@Setter @Getter
public class SolrjQueryFactory {
	private static Map<String, SolrjQuery> searchServerMap = Collections.synchronizedMap(new HashMap<String, SolrjQuery>());
	
	private static String baseUrl = "http://localhost:18080/solr";
    
	public static SolrjQuery getServer(String coreName) {
		SolrjQuery solrjQuery = null;
		if (searchServerMap.containsKey(coreName)) {
			return searchServerMap.get(coreName);
		}
		solrjQuery = new SolrjQuery();
		solrjQuery.setUrl(baseUrl + "/" + coreName);
		solrjQuery.setSoTimeOut(1000);
		solrjQuery.setConnectionTimeOut(1000);
		solrjQuery.setMaxConnectionsPerHost(100);
		solrjQuery.setMaxTotalConnections(100);
		solrjQuery.setMaxRetries(1);
		try {
			solrjQuery.init();
			searchServerMap.put(coreName, solrjQuery);
			return solrjQuery;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}
