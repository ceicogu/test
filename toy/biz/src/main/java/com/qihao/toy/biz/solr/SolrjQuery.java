package com.qihao.toy.biz.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.CollectionUtils;
@Setter @Getter
public class SolrjQuery {
	   private String url;  
	    private Integer soTimeOut;  
	    private Integer connectionTimeOut;  
	    private Integer maxConnectionsPerHost;  
	    private Integer maxTotalConnections;  
	    private Integer maxRetries;  
	    private HttpSolrServer solrServer = null;  
	    private final static String ASC = "asc";  
	  
	    public void init() throws MalformedURLException {  
	        solrServer = new HttpSolrServer(url);  
	        solrServer.setSoTimeout(soTimeOut);  
	        solrServer.setConnectionTimeout(connectionTimeOut);  
	        solrServer.setDefaultMaxConnectionsPerHost(maxConnectionsPerHost);  
	        solrServer.setMaxTotalConnections(maxTotalConnections);  
	        solrServer.setFollowRedirects(false);  
	        solrServer.setAllowCompression(true);  
	        solrServer.setMaxRetries(maxRetries);  
	    }  
	    public void write(Map<String,String>propertyMap){
            SolrInputDocument doc1 = new SolrInputDocument();
            for(String key : propertyMap.keySet()) {
            	doc1.addField(key, propertyMap.get(key));
            }
            try {
				solrServer.add(doc1);
	            solrServer.commit();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    public void write(Object obj){
	    	try {
				solrServer.addBean(obj);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    public SolrDocumentList query(Map<String, String> propertyMap,  
	            Map<String, String> compositorMap, Integer startIndex, Integer pageSize)  
	            throws Exception {  
	        SolrQuery query = new SolrQuery();  
	        // 设置搜索字段  
	        if (null == propertyMap) {  
	            throw new Exception("搜索字段不可为空!");  
	        } else {  
	            for (Object o : propertyMap.keySet()) {  
	                StringBuffer sb = new StringBuffer();  
	                sb.append(o.toString()).append(":");  
	                sb.append(propertyMap.get(o));  
	                String queryString = addBlank2Expression(sb.toString());  
	                query.setQuery(queryString);  
	            }  
	        }  
	        // 设置排序条件  
	        if (!CollectionUtils.isEmpty(compositorMap)) {  
	            for (Object co : compositorMap.keySet()) {  
	                if (ASC == compositorMap.get(co)  
	                        || ASC.equals(compositorMap.get(co))) {  
	                    query.addSort(co.toString(), SolrQuery.ORDER.asc);  
	                } else {  
	                    query.addSort(co.toString(), SolrQuery.ORDER.desc);  
	                }  
	            }  
	        }  
	          
	        if (null != startIndex) {  
	            query.setStart(startIndex);  
	        }  
	        if (null != pageSize && 0L != pageSize.longValue()) {  
	            query.setRows(pageSize);  
	        }  
	        query.set("wt", "json");
	        try {  
	            QueryResponse qrsp = solrServer.query(query);  
	            SolrDocumentList docs = qrsp.getResults();  
	            return docs;  
	        } catch (Exception e) {  
	            throw new Exception(e);  
	        }  
	    }  
	  
	    private String addBlank2Expression(String oldExpression) {  
	        String lastExpression;  
	        lastExpression = oldExpression.replace("AND", " AND ").replace("NOT",  
	                " NOT ").replace("OR", " OR ");  
	        return lastExpression;  
	    }  
}
