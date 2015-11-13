package com.ucpaas.im.util;

import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * 日期转换工具类
 *
 */
public class DateUtil {
	public static final String DATE_TIME_NO_SLASH = "yyyyMMddHHmmss";

	public static String format(String formater, Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(formater);
		return sdf.format(date);
	}

}
