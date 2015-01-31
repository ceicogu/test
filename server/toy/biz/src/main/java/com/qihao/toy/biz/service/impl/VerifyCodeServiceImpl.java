package com.qihao.toy.biz.service.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.qihao.shared.base.utils.RandomStringHelper;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.enums.VerifyCodeStatusEnum;
import com.qihao.toy.dal.enums.VerifyCodeTypeEnum;
import com.qihao.toy.dal.persistent.VerifyCodeMapper;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {
	@Autowired
	private VerifyCodeMapper verifyCodeMapper;
	@Autowired
	private MessageChannelService messageChannelManager;
	public boolean createVerifyCode( Long invitorId,VerifyCodeTypeEnum codeType, String mobile) {
		return this.createVerifyCode(invitorId,codeType, mobile, null, null);
	}
	public boolean createVerifyCode( Long invitorId,VerifyCodeTypeEnum codeType, String mobile, Integer codeLength) {
		return this.createVerifyCode(invitorId, codeType, mobile, codeLength, null);
	}
	public boolean createVerifyCode( Long invitorId, VerifyCodeTypeEnum codeType, String mobile,  Integer codeLength,Integer duration) {
		Preconditions.checkArgument(StringUtils.isNotBlank(mobile),"请指定手机号码！");

		VerifyCodeDO verifyCode = new VerifyCodeDO();
		verifyCode.setType(codeType.numberValue());
		verifyCode.setMobile(mobile);
		verifyCode.setDuration(null==duration?-1:duration);		
		verifyCode.setGmtInvited(new Date());
		verifyCode.setInvitorId(invitorId);
		//生成验证码
		String code = RandomStringHelper.getRandomNum(null==codeLength? 6: codeLength);
		verifyCode.setCode(code);
		verifyCode.setStatus(VerifyCodeStatusEnum.Initial.numberValue());
		 verifyCodeMapper.insert(verifyCode) ;
		if(null != verifyCode.getId()) {
			//发送验证码给用户
			String contentTpl = messageChannelManager.getMessageTemplate(VerifyCodeTypeEnum.Reg_VerifyCode);
			String content = String.format(contentTpl, code);
			messageChannelManager.sendMessage(verifyCode.getMobile(), content);
		}
		return true;
	}

	public boolean checkVerifyCode(VerifyCodeTypeEnum codeType, String mobile, String code) {
		VerifyCodeDO verifyCodeDO = verifyCodeMapper.getValidItem(VerifyCodeTypeEnum.Reg_VerifyCode.numberValue(), mobile, code);
		Preconditions.checkArgument(null != verifyCodeDO,"验证码错误！");
 	
    	if(!verifyCodeDO.getStatus().equals(VerifyCodeStatusEnum.Initial.numberValue())) {
    		throw new IllegalArgumentException("验证码已失效！");
    	}
    	if(verifyCodeDO.getDuration() ==null || verifyCodeDO.getDuration().equals(-1)) {
    		return true;
    	}    	
    	long diff =   (new Date()).getTime() - verifyCodeDO.getGmtCreated().getTime();
    	if(diff > verifyCodeDO.getDuration()) {
    		verifyCodeMapper.updateStatusById(verifyCodeDO.getId(),VerifyCodeStatusEnum.Invalid.numberValue());
    		throw new IllegalArgumentException("验证码已过期失效！");
    	}
    	return true;
	}

	public boolean comfirmVerifyCode(VerifyCodeTypeEnum codeType, String mobile, String code) {
		VerifyCodeDO verifyCodeDO = verifyCodeMapper.getValidItem(VerifyCodeTypeEnum.Reg_VerifyCode.numberValue(), mobile, code);
    	if(null == verifyCodeDO){
    		throw new IllegalArgumentException("验证码错误！");
    	}    	
    	return verifyCodeMapper.updateStatusById(verifyCodeDO.getId(),VerifyCodeStatusEnum.Verified.numberValue()) > 0;
	}

}
