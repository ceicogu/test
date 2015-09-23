package com.qihao.toy.biz.solr;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrDocument;

import com.qihao.toy.biz.solr.domain.ResourceSolrDO;

public interface SolrjOperator {

    /**
     * 写入单条纪录(加入单条数据索引)
     * @param coreName
     * @param propertyMap
     * @return
     * @throws Exception
     */
    public void writeSolrDocument(String coreName, Map<String,String> propertyMap)  throws Exception;

	/**
	 * 获得语句分词结果
	 * @param sentence
	 * @return
	 * @throws Exception
	 */
    public List<String> anlysisSolrResult(String coreName, String sentence)  throws Exception;  
	 
    /** 
     * 获得搜索结果 
     *  
     * @param coreName			coreName
     * @param propertyDO 		搜索条件对象
     * @param compositorDO 		排序条件对象
     * @param startIndex 		分页查询－开始点
     * @param pageSize 			分页查询－每页记录数
     * @return 
     * @throws Exception 
     */  
    public SolrDocumentList querySolrResult(String coreName, Object propertyDO,  Object compositorDO, List<String> filterFields, Integer startIndex, Integer pageSize)  throws Exception;  
  
    /** 
     * 获得搜索结果条数 
     *  
     * @param propertyDO 
     * @param compositorDO 
     * @return 
     * @throws Exception 
     */  
    public Long querySolrResultCount(String coreName,ResourceSolrDO propertyDO, Object compositorDO) throws Exception;  
}
