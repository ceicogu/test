package com.qihao.toy.dal.persistent;

import java.util.List;

import com.qihao.toy.dal.domain.ToyDO;

/**
 * 用户玩具信息基础表DAO
 */
public interface ToyMapper extends CRUDMapper<ToyDO> {
	//根据toySN获取用户玩具基本信息
	ToyDO getItemByToySN(String toySN);
	//获取我管理的Toy列表
	List<ToyDO> getMyManageToys(long myId);
}
