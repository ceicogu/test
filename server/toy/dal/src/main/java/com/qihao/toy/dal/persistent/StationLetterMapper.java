package com.qihao.toy.dal.persistent;


import java.util.List;
import java.util.Map;

import com.qihao.toy.dal.domain.StationLetterDO;

public interface StationLetterMapper extends CRUDMapper<StationLetterDO> {
	/**
	 * acceptorId&senderId
	 * @param map
	 * @return
	 */
	List<StationLetterDO> getAllForO2O(Map<String, String> map);
	/**
	 * 获取每个sender最新一条记录
	 * @param searchDO
	 * @return
	 */
	List<StationLetterDO> getLastItemsBySenderIds(StationLetterDO searchDO);
}
