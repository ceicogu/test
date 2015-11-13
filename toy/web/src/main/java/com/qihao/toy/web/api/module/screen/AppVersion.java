package com.qihao.toy.web.api.module.screen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.alibaba.citrus.turbine.dataresolver.Param;
import com.alibaba.fastjson.JSON;
import com.qihao.shared.base.DataResult;
import com.qihao.toy.biz.service.AppVersionService;
import com.qihao.toy.biz.utils.AnnexUtils;
import com.qihao.toy.dal.domain.AppVersionDO;
import com.qihao.toy.web.api.base.BaseApiScreenAction;

public class AppVersion extends BaseApiScreenAction{
	
	@Autowired
	private AppVersionService appVersionService;
	
	public void doUploadNewVersion(ParameterParser requestParams, @Param("authToken") String authToken,
			@Param("appFile") FileItem appFile, @Param("deviceType") Integer deviceType,
			@Param("platformType") Integer platformType, @Param("versionCode") Integer versionCode,
			@Param("versionName") String versionName, @Param("forcedUpdate") boolean forcedUpdate,
			@Param("description") String description) throws IOException {
    	Assert.notNull(currentUser, "用户未登录!");
    	DataResult<Map<String,Object>> result =  new DataResult<Map<String,Object>>();
    	FileItem fileItem = requestParams.getFileItem("appFile");
		if (null == fileItem) {
			result.setSuccess(false);
			result.setErrorCode(1000);
			result.setMessage("请指定上传文件！");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
    	AnnexUtils.saveAppFile(fileItem);
    	AppVersionDO appVersion = new AppVersionDO();
    	appVersion.setVersionCode(versionCode);
    	appVersion.setVersionName(versionName);
    	appVersion.setDeviceType(deviceType);
    	appVersion.setPlatformType(platformType);
    	appVersion.setForcedUpdate(forcedUpdate);
    	appVersion.setDescription(description);
    	appVersion.setPublishTime(new Date());
    	String downloadUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getLocalPort() +"/" + request.getContextPath() +"/api/appVersion/download.json?file=" + URLDecoder.decode(appFile.getName(), "utf-8");
    	appVersion.setDownloadUrl(downloadUrl);
    	appVersionService.addNewVersionApp(appVersion);
    	result.setSuccess(true);
		result.setMessage("OK");
		response.getWriter().println(JSON.toJSONString(result));
    }
	
	public void doDownload(ParameterParser requestParams, @Param("file")String file) throws Exception{
		Assert.notNull(currentUser, "用户未登录!");
		OutputStream out = response.getOutputStream();
		String fileName = URLDecoder.decode(file, "utf-8");
        File downloadFile = AnnexUtils.getAppFile(fileName);
        if(null == downloadFile){
            out.write("Please input filename".getBytes());
            out.flush();
            out.close();
            return;
        }            
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(downloadFile));
        byte[] buf = new byte[2048];  
        
        int length = in.read(buf);
        int totalLen = 0;
        while (length != -1) {
        	totalLen += length;
            out.write(buf, 0, length);  
            length = in.read(buf);  
        }  
        in.close();  
        response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");
        response.setContentLength(totalLen);
        out.close();
	}
	
	public void doCheckUpdate(ParameterParser requestParams, @Param("authToken") String authToken,
			@Param("deviceType") Integer deviceType, @Param("platformType") Integer platformType) throws IOException {
		Assert.notNull(currentUser, "用户未登录!");
		DataResult<AppVersionDO> result = new DataResult<>();
		if(deviceType == null || platformType == null){
			result.setSuccess(false);
			result.setMessage("invalid request params");
			response.getWriter().println(JSON.toJSONString(result));
			return;
		}
		AppVersionDO appVersion = appVersionService.findLatestVersionApp(deviceType, platformType);
		result.setSuccess(true);
		result.setMessage("OK");
		result.setData(appVersion);
		response.getWriter().println(JSON.toJSONString(result));
	}

}
