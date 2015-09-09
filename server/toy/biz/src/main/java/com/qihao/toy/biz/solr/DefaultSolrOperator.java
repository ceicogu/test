package com.qihao.toy.biz.solr;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import com.qihao.toy.biz.solr.domain.ResourceSolrDO;

@Slf4j
public class DefaultSolrOperator implements SolrjOperator {

	public void writeSolrDO(Object propertyDO){

	}
    public SolrDocumentList querySolrResult(String coreName, Object propertyDO,  Object compositorDO, List<String> fields, Integer startIndex, Integer pageSize)  
            throws Exception {  
        Map<String, String> propertyMap = new TreeMap<String, String>();  
        Map<String, String> compositorMap = new TreeMap<String, String>();  
        try {
        	if(null != propertyDO)  propertyMap = SolrjCommonUtil.getSearchProperty(propertyDO);  
            if(null != compositorDO)  compositorMap = SolrjCommonUtil.getSearchProperty(compositorDO);  
        } catch (Exception e) {  
            log.error("SolrjCommonUtil.getSearchProperty() is error !"+ e);  
        }  
        SolrjQuery solrjQuery = SolrjQueryFactory.getServer(coreName);
        SolrDocumentList solrDocumentList = solrjQuery.query(propertyMap, compositorMap,  fields, startIndex, pageSize);  

        return solrDocumentList;  
    }  
 
    public Long querySolrResultCount(String coreName,ResourceSolrDO propertyDO, Object compositorDO) throws Exception {  
        Map<String, String> propertyMap = new TreeMap<String, String>();  
        Map<String, String> compositorMap = new TreeMap<String, String>();  
        try {  
            propertyMap = SolrjCommonUtil.getSearchProperty(propertyDO);  
            compositorMap = SolrjCommonUtil.getSearchProperty(compositorDO);  
        } catch (Exception e) {  
            log.error("SolrjCommonUtil.getSearchProperty() is error !" + e);  
        }  
        SolrjQuery solrjQuery = SolrjQueryFactory.getServer(coreName);
        SolrDocumentList solrDocument = solrjQuery.query(propertyMap, compositorMap,  null, null, null);  
        return solrDocument.getNumFound();  
    }
	public List<String> anlysisSolrResult(String coreName,String sentence) throws Exception {
        SolrjQuery solrjQuery = SolrjQueryFactory.getServer(coreName);
        return solrjQuery.analysis(sentence);  
	}  

}
