package com.qihao.toy.biz.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.util.CollectionUtils;

import com.alibaba.common.lang.StringUtil;
import com.google.common.base.Joiner;
@Slf4j
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
	    /**
	     * 获取语句分词结果
	     * @param sentence
	     * @return
	     */
	    public List<String> analysis(String sentence) {
	        FieldAnalysisRequest request = new FieldAnalysisRequest("/analysis/field");
	        request.addFieldName("title");// 字段名，随便指定一个支持中文分词的字段
	        request.setFieldValue("");// 字段值，可以为空字符串，但是需要显式指定此参数
	        request.setQuery(sentence);
	       
	        FieldAnalysisResponse response = null;
	        try {
	            response = request.process(solrServer);
	        } catch (Exception e) {
	            log.error("获取查询语句的分词时遇到错误", e);
	        }

	        List<String> results = new ArrayList<String>();
	        Iterator<AnalysisPhase> it = response.getFieldNameAnalysis("title")
	                .getQueryPhases().iterator();
	        while(it.hasNext()) {
	          AnalysisPhase pharse = (AnalysisPhase)it.next();
	          List<TokenInfo> list = pharse.getTokens();
	          for (TokenInfo info : list) {
	              results.add(info.getText());
	          }
	        }
	        
	        return results;
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
	    public SolrDocumentList query(
	    		Map<String, String> propertyMap, 
	    		Map<String, String> compositorMap, 
	    		List<String>  fields,
	    		Integer startIndex, Integer pageSize)  
	            throws Exception {  
	        SolrQuery query = new SolrQuery();  
	        // 设置搜索字段  
	        if (null == propertyMap) {  
	            throw new Exception("搜索字段不可为空!");  
	        } else {  
	        	StringBuffer sb = new StringBuffer();  
	            for (Object o : propertyMap.keySet()) {
	                if(!StringUtil.isBlank(sb.toString())) {
	                	sb.append(" AND ");
	                }
	                sb.append(o.toString()).append(":").append(propertyMap.get(o));
	            }
		           
                
                String queryString = addBlank2Expression(sb.toString());  
                query.setQuery(queryString);  
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
	        //设置索引显示哪些fileds
	        if(!CollectionUtils.isEmpty(fields)){
	        	String fq = Joiner.on(",").join(fields);
	        	query.addField(fq);
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
