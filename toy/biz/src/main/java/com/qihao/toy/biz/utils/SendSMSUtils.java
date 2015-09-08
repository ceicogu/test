package com.qihao.toy.biz.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.google.common.collect.Maps;
import com.qihao.shared.base.api.internal.util.WebUtils;

@Slf4j
public class SendSMSUtils {
	private final static String cdkey = "0SDK-EBB-0130-JIULT";
	private final static String password = "111111";
	private final static String baseUrl = "http://sdkhttp.eucp.b2m.cn/sdkproxy/";
	private final static Map<String,String> actionMaps = Maps.newConcurrentMap();
	static {
		actionMaps.put("regist", baseUrl+"regist.action");
		actionMaps.put("registdetailinfo", baseUrl+"registdetailinfo.action");	
		actionMaps.put("sendsms", baseUrl+"sendsms.action");
	}
	public static void main(String[] args) throws UnsupportedEncodingException,
	DocumentException {
		regist();
		registdetailinfo();
		Map<String, Object> parameter = Maps.newConcurrentMap();
		parameter.put("code", "18600361168");
		
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource resource = resourcePatternResolver.getResource("classpath:template/InviteCode.tpl.vm");
		System.out.print(resource.getFilename());
		System.out.print(resource.getDescription());
		
		String content = VelocityUtils.evaluate("【奇好故事机】邀请您成为好友！邀请码是：${code}，请尽快注册防止失效", parameter);
		
		//String content = VelocityUtils.mergeTemplate("/Users/luqiao/workspace/qihao/server/toy/biz/src/main/resources/template/InviteCode.tpl.vm", "utf-8", parameter);
		System.out.print(content);
		//sendsms("18600361168","【奇好故事机】邀请您成为好友！邀请码是：，请尽快注册防止失效",null,"1","1");
	}

	public static boolean regist(){
		Map<String,String> param = Maps.newConcurrentMap();
		param.put("cdkey", cdkey);
		param.put("password", password);
		
		Map<String,Object> result = exec("regist",param);
		if(null == result) {
			return false;
		}
		return "0".equals(result.get("error"));
	}
	public static boolean registdetailinfo(){
		Map<String,String> param = Maps.newConcurrentMap();
		param.put("cdkey", cdkey);
		param.put("password", password);
		param.put("ename", "北京奇好云趣信息技术有限公司");
		param.put("linkman", "王红军");
		param.put("phonenum", "18618406934");
		param.put("mobile", "18618406934");
		param.put("email", "wanghongjun@qihsaobox.com");
		param.put("fax", "18618406934");
		param.put("address", "北京市朝阳区大");
		param.put("postcode", "100021");

		
		Map<String,Object> result = exec("registdetailinfo",param);
		if(null == result) {
			return false;
		}
		return "0".equals(result.get("error"));
	}	
	public static boolean sendsms(String phone, String message){
		return sendsms(phone,message,null,null,null);
	}
	public static boolean sendsms(String phone, String message,String addserial, String seqid,String smspriority){
		Map<String,String> param = Maps.newConcurrentMap();
		param.put("cdkey", cdkey);
		param.put("password", password);
		param.put("phone", phone);
		param.put("message", message);
		if(!StringUtils.isBlank(addserial)){
			param.put("addserial", addserial);
		}
		if(!StringUtils.isBlank(seqid)){
			param.put("seqid", seqid);	
		}
		if(StringUtils.isNumeric(smspriority)){
			param.put("smspriority", smspriority);	
		} else {
			param.put("smspriority", "1");	
		}
		
		Map<String,Object> result = exec("sendsms",param);
		if(null == result) {
			return false;
		}
		return "0".equals(result.get("error"));
	}

	public static Map<String,Object> exec(String actionName, Map<String,String> param){
		String url = actionMaps.get(actionName);
		if(null == url) {
			log.error("action no exist！");
			return null;
		}
		try {
			String resp = WebUtils.doGet(url, param);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> xml2map = (Map<String, Object>)XmlUtils.xml2map(resp.trim());
			return xml2map;
		} catch (IOException e) {
			log.error("发送消息异常！exception={}",e);
			return null;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
	}
}
