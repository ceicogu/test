package com.qihao.toy.biz.service.impl;

import com.alibaba.fastjson.JSON;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.biz.service.BabyHelperService;
import com.qihao.toy.biz.utils.MiPushUtils;
import com.qihao.toy.dal.domain.BabyHelperDO;
import com.qihao.toy.dal.domain.MiPushCommandMessage;
import com.qihao.toy.dal.domain.UserDO;
import com.qihao.toy.dal.domain.enums.CommandTypeEnum;
import com.qihao.toy.dal.domain.enums.MiContentTypeEnum;
import com.qihao.toy.dal.domain.enums.OperateTypeEnum;
import com.qihao.toy.dal.persistent.BabyHelperMapper;
import com.xiaomi.xmpush.server.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BabyHelperServiceImpl implements BabyHelperService {
	@Autowired
	private BabyHelperMapper babyHelperMapper;
	@Autowired
	private AccountService accountService;

	public Long insert(BabyHelperDO helper) {
		babyHelperMapper.insert(helper);
		sendMessageToToy(OperateTypeEnum.ADD,helper);
		return helper.getId();
	}

	public Boolean update(Long id, BabyHelperDO helper) {
		helper.setId(id);
		boolean bOK =  babyHelperMapper.update(helper);
		sendMessageToToy(OperateTypeEnum.UPDATE,helper);
		return bOK;
	}

	public Boolean deleteById(Long id) {
		BabyHelperDO helper = this.getById(id);
		boolean bOK =  babyHelperMapper.deleteById(id);
		sendMessageToToy(OperateTypeEnum.DELETE,helper);
		return bOK;
	}

	public BabyHelperDO getById(Long id) {
		return babyHelperMapper.getById(id);
	}

	public List<BabyHelperDO> getAll(BabyHelperDO helper) {
		return babyHelperMapper.getAll(helper);
	}
	private String sendMessageToToy(OperateTypeEnum optType,BabyHelperDO helper ){
		//通知Toy更新
		MiPushCommandMessage miPushCmdMessage = new MiPushCommandMessage();
		miPushCmdMessage.setSenderId(helper.getOperatorId());
		miPushCmdMessage.setCmdType(CommandTypeEnum.HELPER_CONTORL);
		miPushCmdMessage.setOptType(optType);
		miPushCmdMessage.setCmdId(helper.getId());
		miPushCmdMessage.setCmdContent(JSON.toJSONString(helper));
		try {
			Message message = MiPushUtils.buildMessage( 
					CommandTypeEnum.HELPER_CONTORL.name(),
					MiContentTypeEnum.HELPER.name(),
					helper);
			UserDO user = accountService.getUser(helper.getToyUserId());
			String miMessageId = MiPushUtils.sendMessage(message, user.getDeviceToken());
			if(null != miMessageId) {

			}else {

			}
			return miMessageId;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
}
