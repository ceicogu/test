package com.qihao.toy.biz.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qihao.toy.biz.service.BabyHelperService;
import com.qihao.toy.biz.utils.MiPushUtils;
import com.qihao.toy.dal.domain.BabyHelperDO;
import com.qihao.toy.dal.domain.MiPushCommandMessage;
import com.qihao.toy.dal.enums.MediaTypeEnum;
import com.qihao.toy.dal.enums.MiCommandTypeEnum;
import com.qihao.toy.dal.enums.MiContentTypeEnum;
import com.qihao.toy.dal.persistent.BabyHelperMapper;
import com.xiaomi.xmpush.server.Message;

@Service
public class BabyHelperServiceImpl implements BabyHelperService {
	@Autowired
	private BabyHelperMapper babyHelperMapper;

	public Long insert(BabyHelperDO helper) {
		babyHelperMapper.insert(helper);
		//通知Toy更新
		MiPushCommandMessage miPushCmdMessage = new MiPushCommandMessage();
		miPushCmdMessage.setSenderId(helper.getOperatorId());
		miPushCmdMessage.setType(MiCommandTypeEnum.DOWNLOAD);
		miPushCmdMessage.setMediaType(MediaTypeEnum.SOUND);
		miPushCmdMessage.setMsgId(helper.getId());
		miPushCmdMessage.setMsgContent(helper.getTags());
		miPushCmdMessage.setMsgUrl(helper.getUrl());
		try {
			Message message = MiPushUtils.buildMessage( 
					MiCommandTypeEnum.DOWNLOAD.name(),
					MiContentTypeEnum.HELPER.name(),
					helper);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return helper.getId();
	}

	public Boolean update(Long id, BabyHelperDO helper) {
		helper.setId(id);
		return babyHelperMapper.update(helper);
	}

	public Boolean deleteById(Long id) {
		return babyHelperMapper.deleteById(id);
	}

	public BabyHelperDO getById(Long id) {
		return babyHelperMapper.getById(id);
	}

	public List<BabyHelperDO> getAll(BabyHelperDO helper) {
		return babyHelperMapper.getAll(helper);
	}

}
