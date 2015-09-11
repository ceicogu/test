package com.qihao.toy.web.valve;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.support.AbstractValve;
import com.alibaba.citrus.turbine.TurbineRunData;
import com.alibaba.citrus.turbine.util.TurbineUtil;
import com.alibaba.citrus.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.qihao.shared.base.SimpleResult;
import com.qihao.toy.biz.service.AccountService;
import com.qihao.toy.dal.domain.UserDO;


public class AuthorizationValve extends AbstractValve {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationValve.class);

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private AccountService accountService;
    //无需进行authToken验证的api白名单
    private final static List<String>		targetWhiteList = Lists.newArrayList();
    static{
    	targetWhiteList.add("account_login");
    	targetWhiteList.add("account_register");
    	targetWhiteList.add("account_validateQrCode");
    	targetWhiteList.add("account_createVerifyCode");
    	targetWhiteList.add("account_checkInvitionCode");
    }
    
	public void invoke(PipelineContext pipelineContext) throws Exception {

        try {
            TurbineRunData rundata = TurbineUtil.getTurbineRunData(request);
            String target = rundata.getTarget();
            String nowBucPermisson = StringUtil.replaceChar(target.substring(1), '/', '_');
            //后缀为.json的特殊处理
            if (nowBucPermisson.lastIndexOf(".") >= 0) {
                String suffix = StringUtil.substringAfterLast(nowBucPermisson, ".");
                if (suffix.equals("json")) {
                    nowBucPermisson = StringUtil.substringBeforeLast(nowBucPermisson, ".");
                }
            }

            //排除JS CSS 图片和白名单页面等
            if (nowBucPermisson.lastIndexOf(".") <= 0 && !isInWhite(nowBucPermisson)) {
            	String authToken = rundata.getParameters().getString("authToken");
            	try{
	            	UserDO user = checkUserPermission(authToken);
	            	request.setAttribute("currentUser", user);
            	}
            	catch(Exception e) {
            		SimpleResult result = new SimpleResult();
                    result.setSuccess(false);
                    result.setErrorCode(1000);
                    result.setMessage("请登录！原因:"+e.getMessage());
                    String jsonContent = JSON.toJSONString(result);
                    response.setContentLength(jsonContent.length());
                    response.setContentType("application/json; charset=utf-8");
                    response.setCharacterEncoding("utf-8");
                    response.getWriter().print(jsonContent);        
                    pipelineContext.breakPipeline(1);//level=1，中断上一级pipeline
            	}            	
            }            
            pipelineContext.invokeNext();
        } catch (Exception e) {
            logger.error("check_permisson_error" + e);
        }
    }
	/**
	 * 检查用户权限
	 * @param token
	 * @return
	 */
    public UserDO checkUserPermission(String authToken) {
    	Preconditions.checkArgument(StringUtils.isNotBlank(authToken),"AuthToken不能为空");

    	UserDO userDO = accountService.validateAuthToken(authToken);
    	return userDO;
    }
    //白名单列表
    public boolean isInWhite(String nowBucPermisson) {    	
        if (StringUtils.isBlank(nowBucPermisson)) {
            return false;
        }
        return targetWhiteList.contains(nowBucPermisson);
    }
}
