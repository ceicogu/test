package com.qihao.toy.web.valve;

import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.citrus.service.pipeline.PipelineContext;
import com.alibaba.citrus.service.pipeline.Valve;
import com.alibaba.citrus.service.requestcontext.parser.ParameterParser;
import com.qihao.shared.base.DataResult;
import com.qihao.shared.base.SimpleResult;
import com.qihao.shared.base.utils.MD5Algorithm;
import com.qihao.toy.biz.config.GlobalConfig;

@Slf4j
public class SignatureCheckValve implements Valve {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	private HttpServletResponse response;
	@Autowired
	protected GlobalConfig globalConfig;

	public void invoke(PipelineContext pipelineContext) throws Exception {
		System.out.println("valve started.");
		Map<String, String[]> paramMaps = request.getParameterMap();

		pipelineContext.invokeNext(); // 调用后序 valves
		System.out.println("valve ended.");
	}

	protected SimpleResult signature(ParameterParser requestParams) {
		String[] keyList = requestParams.getKeys();
		DataResult<Map<String, Object>> result = new DataResult<Map<String, Object>>();

		Map<String, String> args = new TreeMap<String, String>();
		for (String key : keyList) {
			if (key.equalsIgnoreCase("sign"))
				continue;
			args.put(key, requestParams.getString(key));
		}
		StringBuilder buf = new StringBuilder();
		for (Map.Entry<String, String> entry : args.entrySet()) {
			if (StringUtils.isNotBlank(entry.getValue())) {
				buf.append(entry.getKey()).append(entry.getValue());
			}
		}
		if (!StringUtils.isNotBlank(buf.toString())) {
			log.warn("参数错误！");
			result.setSuccess(false);
			result.setErrorCode(1000);
			result.setMessage("参数错误!");
			return result;
		}
		// 2.签名比对
		String sign = requestParams.getString("sign");
		String newSign = null;
		try {
			newSign = MD5Algorithm.digest(buf.toString(),
					globalConfig.getMd5Key());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		// if(null == newSign || !sign.equals(newSign)) {
		// log.warn("签名失败！");
		// result.setSuccess(false);
		// result.setErrorCode(1000);
		// result.setMessage("签名失败！");
		// return result;
		// }

		result.setSuccess(true);
		return result;
	}
}
