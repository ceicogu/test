package com.qihao.toy.web.base;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.service.requestcontext.parser.ParserRequestContext;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.shared.base.utils.MD5Algorithm;
import com.qihao.toy.biz.config.GlobalConfig;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.dal.domain.UserDO;

@Slf4j
public class BaseScreenAction {
    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    private ParserRequestContext parser;
    
    @Autowired
    protected AccountService accountService;
    @Autowired
    protected GlobalConfig globalConfig;
    protected UserDO currentUser = null;
    
    /** 此方法会在所有的event handler之前执行。 */
    public void beforeExecution() {
        response.setContentType("application/json");
        currentUser = (UserDO) request.getAttribute("currentUser");
    }

    /** 此方法会在所有的event handler之后执行。 */
    public void afterExecution() throws IOException {
        response.flushBuffer(); // 此调用并非必须，只是用来演示afterExecution方法而已
    }
    protected SimpleResult signature(ParameterParser requestParams) {
    	String[] keyList = requestParams.getKeys();
    	DataResult<Map<String,Object>> result =new DataResult<Map<String,Object>>();

    	Map<String,String> args = new TreeMap<String,String>();
        for(String key : keyList) {
        	if(key.equalsIgnoreCase("sign")) continue;
            args.put(key,  requestParams.getString(key));
        }
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> entry : args.entrySet()) {
            if(StringUtils.isNotBlank(entry.getValue())) {
                buf.append(entry.getKey()).append(entry.getValue());
            }
        }
        if(!StringUtils.isNotBlank(buf.toString())) {
            log.warn("参数错误！");
            result.setSuccess(false);
            result.setErrorCode(1000);
            result.setMessage("参数错误!");
    		return result;
        } 
        //2.签名比对
        String sign = requestParams.getString("sign");        
        String newSign = null;
        try {
        	newSign = MD5Algorithm.digest(buf.toString(), globalConfig.getMd5Key());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
//		if(null == newSign || !sign.equals(newSign)) {
//			    log.warn("签名失败！");
//			    result.setSuccess(false);
//			    result.setErrorCode(1000);
//			    result.setMessage("签名失败！");
//				return result;
//		}
    	
        result.setSuccess(true);
        return result;
    }
    
    protected boolean isAuthed() throws IOException {
    	return  null != currentUser;
    }
}
