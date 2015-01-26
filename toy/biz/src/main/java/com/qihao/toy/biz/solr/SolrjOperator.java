package com.qihao.toy.biz.solr;

import java.util.List;

import com.qihao.toy.biz.solr.domain.ResourceSolrDO;

public interface SolrjOperator {
	 
    /** 
     * 获得搜索结果 
     *  
     * @param propertyDO 			搜索条件对象
     * @param compositorDO 		排序条件对象
     * @param startIndex 				分页查询－开始点
     * @param pageSize 					分页查询－每页记录数
     * @return 
     * @throws Exception 
     */  
    public List<Object> querySolrResult(Object propertyDO,  Object compositorDO, Integer startIndex, Integer pageSize)  throws Exception;  
  
    /** 
     * 获得搜索结果条数 
     *  
     * @param propertyDO 
     * @param compositorDO 
     * @return 
     * @throws Exception 
     */  
    public Long querySolrResultCount(ResourceSolrDO propertyDO, Object compositorDO) throws Exception;  
}
