package com.qihao.toy.dal.persistent;

import org.apache.ibatis.annotations.Param;

import com.qihao.toy.dal.domain.VerifyCodeDO;

public interface VerifyCodeMapper extends CRUDMapper<VerifyCodeDO> {
	VerifyCodeDO getValidItem(@Param("type") Integer type, @Param("mobile") String mobile,@Param("code") String code);
	int updateStatusById(@Param("id") Long id,@Param("status") Integer status);
}
