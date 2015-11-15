package com.qihao.toy.biz.service;

import java.util.List;
import java.util.Map;

import com.qihao.shared.base.DataResult;
import com.qihao.toy.dal.domain.StationLetterDO;

public interface StationLetterService {
	/**
	 * 创建站内消息
	 * @param letter
	 * @return
	 */
	DataResult<Long> createLetter(StationLetterDO letter);
	DataResult<Long> createLetter(long senderId, int acceptorType, long acceptorId,	StationLetterDO.MediaType type, String content, String url);
	/**
	 * 按接收者获取站内消息
	 * @param acceptorType
	 * @param acceptorId
	 * @return
	 */
	DataResult<List<StationLetterDO>> getMyLetters( int acceptorType, long acceptorId, Integer page , Integer maxPageSize);
	DataResult<List<StationLetterDO>> getMyLetters( long o2oId1, long o2oId2, Integer page , Integer maxPageSize);
	DataResult<List<StationLetterDO>> getMyLetters(StationLetterDO searchDO);
	/**
	 * 获取多个senderId最新一条记录
	 * @param senderIds  必填
	 * @param acceptorType  可空
	 * @param acceptorId    可空
	 * @return
	 */
	DataResult<Map<String,Object>> getLastItemsBySenderIds(List<Long> senderIds, Integer acceptorType, Long acceptorId);
	/**
	 * 按站内信ID获取消息具体内容
	 * @param letterId
	 * @return
	 */
	DataResult<StationLetterDO> getMyLetter(long letterId);
}
