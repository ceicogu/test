package com.qihao.toy.biz.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.qihao.shared.base.utils.RandomStringHelper;
import com.qihao.toy.biz.service.MessageChannelService;
import com.qihao.toy.biz.service.VerifyCodeService;
import com.qihao.toy.dal.domain.VerifyCodeDO;
import com.qihao.toy.dal.persistent.VerifyCodeMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class VerifyCodeServiceImpl implements VerifyCodeService {
	@Autowired
	private VerifyCodeMapper verifyCodeMapper;
	@Autowired
	private MessageChannelService messageChannelManager;
	public boolean createVerifyCode( Long invitorId,VerifyCodeDO.VerifyCodeType codeType, String mobile) {
		return this.createVerifyCode(invitorId,codeType, mobile, null, null);
	}
	public boolean createVerifyCode( Long invitorId,VerifyCodeDO.VerifyCodeType codeType, String mobile, Integer codeLength) {
		return this.createVerifyCode(invitorId, codeType, mobile, codeLength, null);
	}
	public boolean createVerifyCode( Long invitorId, VerifyCodeDO.VerifyCodeType codeType, String mobile,  Integer codeLength,Integer duration) {
		Preconditions.checkArgument(StringUtils.isNotBlank(mobile),"请指定手机号码！");

		VerifyCodeDO verifyCode = new VerifyCodeDO();
		verifyCode.setType(codeType);
		verifyCode.setMobile(mobile);
		verifyCode.setDuration(null==duration?-1:duration);		
		verifyCode.setGmtInvited(new Date());
		verifyCode.setInvitorId(invitorId);
		//生成验证码
		String code = RandomStringHelper.getRandomNum(null==codeLength? 6: codeLength);
		verifyCode.setCode(code);
		verifyCode.setStatus(VerifyCodeDO.VerifyCodeStatus.Initial);
		 verifyCodeMapper.insert(verifyCode) ;
		if(null != verifyCode.getId()) {
			//发送验证码给用户
			Map<String,Object> context = Maps.newConcurrentMap();
			context.put("code", code);
			messageChannelManager.sendMessage(codeType,verifyCode.getMobile(), context);
		}
		return true;
	}

	public boolean checkVerifyCode(VerifyCodeDO.VerifyCodeType codeType, String mobile, String code) {
		VerifyCodeDO verifyCodeDO = verifyCodeMapper.getValidItem(codeType, mobile, code);
		Preconditions.checkArgument(null != verifyCodeDO,codeType.name()+" 错误！");
 	
    	if(!verifyCodeDO.getStatus().equals(VerifyCodeDO.VerifyCodeStatus.Initial)) {
    		throw new IllegalArgumentException(" 已失效！");
    	}
    	if(verifyCodeDO.getDuration() ==null || verifyCodeDO.getDuration().equals(-1)) {
    		return true;
    	}    	
    	long diff =   (new Date()).getTime() - verifyCodeDO.getGmtCreated().getTime();
    	if(diff > verifyCodeDO.getDuration()) {
    		verifyCodeMapper.updateStatusById(verifyCodeDO.getId(),VerifyCodeDO.VerifyCodeStatus.Invalid);
    		throw new IllegalArgumentException( " 已过期失效！");
    	}
    	return true;
	}

	public boolean comfirmVerifyCode(VerifyCodeDO.VerifyCodeType codeType, String mobile, String code) {
		VerifyCodeDO verifyCodeDO = verifyCodeMapper.getValidItem(codeType, mobile, code);
    	if(null == verifyCodeDO){
    		throw new IllegalArgumentException(codeType.name()+" 错误！");
    	}    	
    	return verifyCodeMapper.updateStatusById(verifyCodeDO.getId(),VerifyCodeDO.VerifyCodeStatus.Verified) > 0;
	}

}
