package com.qihao.toy.biz.service.impl;

import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.StationLetterService;
import com.qihao.toy.dal.domain.StationLetterDO;
import com.qihao.toy.dal.persistent.MyGroupMemberMapper;
import com.qihao.toy.dal.persistent.StationLetterMapper;


@Slf4j
@Service
public class StationLetterServiceImpl implements StationLetterService {
	@Autowired
	private StationLetterMapper stationLetterMapper;
	@Autowired
	private MyGroupMemberMapper myGroupMemberMapper;

	public DataResult<Long> createLetter(StationLetterDO letter) {
		DataResult<Long> result = new DataResult<Long>();
		stationLetterMapper.insert(letter);
		{//TODO 调用miPush推送消息接口，将消息ID推送给acceptorType的每一个人
			//Message message = MiPushUtils.buildMessage(title, description, messagePayload);
		}		
		result.setSuccess(true);
		result.setData(letter.getId());
		return result;
	}
	public DataResult<Long> createLetter(long senderId, int acceptorType, long acceptorId,	int type, String content, String url) {
		DataResult<Long> result = new DataResult<Long>();
		if(StringUtils.isBlank(content) && StringUtils.isBlank(url)) {
			log.warn("消息内容为空!");
			result.setSuccess(false);
			result.setMessage("消息内容不能为空!");
			return result;
		}

		StationLetterDO stationLetter = new StationLetterDO();
		stationLetter.setSenderId(senderId);
		stationLetter.setAcceptorType(acceptorType);
		stationLetter.setAcceptorId(acceptorId);
		stationLetter.setType(type);
		stationLetter.setContent(content);
		stationLetter.setUrl(url);
		return this.createLetter(stationLetter);
	}

	public DataResult<List<StationLetterDO>> getMyLetters( int acceptorType, long acceptorId) {
		DataResult<List<StationLetterDO>> result = new DataResult<List<StationLetterDO>>();
		StationLetterDO letter = new StationLetterDO();
		List<StationLetterDO> data = null;
		letter.setAcceptorType(acceptorType);
		letter.setAcceptorId(acceptorId);		
		data = stationLetterMapper.getAll(letter);
	
		result.setSuccess(true);
		result.setData(data);
		return result;
	}

	public DataResult<StationLetterDO> getMyLetter(long letterId) {
		DataResult<StationLetterDO> result = new DataResult<StationLetterDO>();
		StationLetterDO data = stationLetterMapper.getById(letterId);	
		result.setSuccess(true);
		result.setData(data);
		return result;
	}
}
