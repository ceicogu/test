package com.ucpaas.im.util;

import com.alibaba.fastjson.JSON;

/**
 * json工具类
 * 
 */
public class JsonUtil {

	public static String toJson(Object obj) {
		return JSON.toJSONString(obj);
	}

}
