package com.qihao.toy.dal.domain;

import java.io.Serializable;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 * BasePOJO 提供toString和序列化
 * 
 * @author 
 */


public abstract class BaseDO implements Serializable {
    private static final long serialVersionUID = 7907563546580885658L;
    @Setter @Getter 
	private Long 	id;
    @Setter @Getter 
    private Date		gmtCreated;
    @Setter @Getter 
    private Date		gmtModified;	
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
    
}