package com.qihao.toy.biz.service;

import java.util.List;

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
	/**
	 * 按站内信ID获取消息具体内容
	 * @param letterId
	 * @return
	 */
	DataResult<StationLetterDO> getMyLetter(long letterId);
}
