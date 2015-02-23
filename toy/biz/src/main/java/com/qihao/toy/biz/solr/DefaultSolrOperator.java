package com.qihao.toy.biz.solr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import com.qihao.toy.biz.solr.domain.ResourceSolrDO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DefaultSolrOperator implements SolrjOperator {

	public void writeSolrDO(Object propertyDO){

	}
    public List<Object> querySolrResult(String coreName, Object propertyDO,  Object compositorDO, List<String> fields, Integer startIndex, Integer pageSize)  
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
        List<Object> resultList = new ArrayList<Object>();  
        for (int i = 0; i < solrDocumentList.size(); i++) {  
            resultList.add(solrDocumentList.get(i));  
        }  
        return resultList;  
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

}
