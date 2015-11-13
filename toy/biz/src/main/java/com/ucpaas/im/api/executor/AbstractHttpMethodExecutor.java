package com.ucpaas.im.api.executor;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucpaas.im.api.message.rsp.Resp;
import com.ucpaas.im.constant.Constants;
import com.ucpaas.im.util.JsonUtil;

public abstract class AbstractHttpMethodExecutor implements HttpMethodExecutor {
	private Logger log = LoggerFactory.getLogger(AbstractHttpMethodExecutor.class);
	protected String targetUrl;
	private String charsetName = "utf-8";

	public AbstractHttpMethodExecutor() {
	}

	public AbstractHttpMethodExecutor(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	@Override
	public String post(String conType, String authorization, final String url,
			String content) {
		//调试debug
	    if (this.log.isDebugEnabled()) {
	        this.log.debug("\n request url:\n{} \n request content:\n{}", url, content);
	    }
	    
		DefaultHttpClient httpClient = null;
		HttpPost post = null;
		try {
			httpClient = new DefaultHttpClient();
			// 设置重试次数为0
			httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
			post = new HttpPost(url);
			post.setHeader("Accept", conType);
			post.setHeader("Content-Type", conType + ";charset=" + this.charsetName);
			post.setHeader("Authorization", authorization);
			//
			post.setEntity(new StringEntity(content, this.charsetName));
			HttpResponse response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity, this.charsetName);
		    if (this.log.isDebugEnabled()) {
		        this.log.debug("\n response:\n{}", resp);
		    }
			return resp;
		} catch (Exception e) {
			this.log.error("调用im-rest接口失败", e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		} finally {
			try {
				if (httpClient != null && httpClient.getConnectionManager() != null) {
					httpClient.getConnectionManager().shutdown();
				}
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public String get(String conType, String authorization, String url,
			String content) {
		//调试debug
	    if (this.log.isDebugEnabled()) {
	        this.log.debug("\n request url:\n{} ", url+content);
	    }
	    
		DefaultHttpClient httpClient = null;
		HttpGet get = null;
		try {
			httpClient = new DefaultHttpClient();
			// 设置重试次数为0
			httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
			get = new HttpGet(url + content);
			get.setHeader("Accept", conType);
			get.setHeader("Content-Type", conType + ";charset=" + this.charsetName);
			get.setHeader("Authorization", authorization);
			//
			HttpResponse response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			String resp = EntityUtils.toString(entity, this.charsetName);
		    if (this.log.isDebugEnabled()) {
		        this.log.debug("\n response:\n{}", resp);
		    }
			return resp;
		} catch (Exception e) {
			this.log.error("调用im-rest接口失败", e);
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(Constants.RESP, new Resp());
			return JsonUtil.toJson(map);
		} finally {
			try {
				if (httpClient != null && httpClient.getConnectionManager() != null) {
					httpClient.getConnectionManager().shutdown();
				}
			} catch (Exception e) {
			}
		}
	}

}
