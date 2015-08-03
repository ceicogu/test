package com.qihao.toy.dal.persistent;

import java.util.List;

import com.qihao.toy.dal.domain.MyToyDO;


public interface MyToyMapper extends CRUDMapper<MyToyDO> {
	/**
	 * 根据toySN获取
	 * @param toySN
	 * @return
	 */
	MyToyDO getItemByToySN(String toySN);
	/**
	 * 获取我管理的toy注册账号ID列表
	 * @param myId
	 * @return
	 */
	List<Long>  getMyToyUserIds(long myId);
}
