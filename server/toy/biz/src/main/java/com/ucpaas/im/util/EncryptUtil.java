package com.ucpaas.im.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;


/**
 * 加密签名工具类
 */
public class EncryptUtil {

	private static final String UTF8 = "utf-8";

	/**
	 * MD5数字签名
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String md5Digest(String str) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] b = md.digest(str.getBytes(UTF8));
		return byte2HexStr(b);
	}

	/**
	 * 字节数组转化为大写16进制字符串
	 * 
	 * @param b
	 * @return
	 */
	private static String byte2HexStr(byte[] b) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			String s = Integer.toHexString(b[i] & 0xFF);
			if (s.length() == 1) {
				sb.append("0");
			}
			sb.append(s.toUpperCase());
		}
		return sb.toString();
	}

	/**
	 * BASE64编码
	 * 
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static String base64Encoder(String src) throws Exception {
		return Base64.encodeBase64String(src.getBytes(UTF8));
//		BASE64Encoder encoder = new BASE64Encoder();
//		return encoder.encode(src.getBytes(UTF8));
	}

}
