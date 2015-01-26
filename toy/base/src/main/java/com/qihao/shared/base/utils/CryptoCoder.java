/*
 * Copyright 2013 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.shared.base.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.alibaba.citrus.util.StringUtil;


/**
 * AES Coder<br/>
 * secret key length: 128bit, default: 128 bit<br/>
 * mode: ECB/CBC/PCBC/CTR/CTS/CFB/CFB8 to CFB128/OFB/OBF8 to OFB128<br/>
 * padding: Nopadding/PKCS5Padding/ISO10126Padding/
 * 
 * @author kaowp 2013-3-7 下午3:37:45
 */
public class CryptoCoder {
	/**
	 * 默认aes加密向量
	 */
	private static final String AES_DEFAULT_SIV = "0102030405060708";
	/**
	 * 默认aes编码
	 */
	public static final String AES_DEFAULT_ENCODE = "utf-8";
	/**
	 * aes方法
	 */
	private static final String AES_METHOD = "AES";
	/**
	 * aes默认"算法/模式/补码方式"
	 */
	private static final String AES_CIPHER = "AES/CBC/PKCS5Padding";


    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    
    
	/**
	 * aes加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @param sIV
	 *            CBC加密向量
	 * @return
	 */
	public static String aesEncrypt(String content, String password, String sIV) {
		return aesEncrypt(content, password, sIV, AES_DEFAULT_ENCODE);
	}
	/**
	 * aes加密
	 * 
	 * @param content
	 *            需要加密的内容
	 * @param password
	 *            加密密码
	 * @param sIV
	 *            CBC加密向量
	 * @param charEncoding
	 *            字符编码
	 * @return
	 */
	public static String aesEncrypt(String content, String password, String sIV, String charEncoding) {
		if (StringUtil.isBlank(content) || StringUtil.isBlank(password)
				|| StringUtil.isBlank(sIV) || StringUtil.isBlank(charEncoding)) {// 参数任意为空则原样返回
			return content;
		}
		try {
			SecretKey secretKey = new SecretKeySpec(password.getBytes(charEncoding), AES_METHOD);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES_METHOD);
			Cipher cipher = Cipher.getInstance(AES_CIPHER);
			// 创建密码器
			byte[] byteContent = content.getBytes(charEncoding);
			IvParameterSpec iv = new IvParameterSpec(sIV.getBytes(charEncoding));// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return Base64.encodeBase64(result);
			// 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * aes解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @param sIV
	 *            CBC加密向量
	 * @param charEncoding
	 *            字符编码
	 * @return
	 */
	public static String aesDecrypt(String content, String password, String sIV) {
		return aesDecrypt(content, password, sIV, AES_DEFAULT_ENCODE);
	}
	/**
	 * aes解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @param sIV
	 *            CBC加密向量
	 * @param charEncoding
	 *            字符编码
	 * @return
	 */
	public static String aesDecrypt(String content, String password, String sIV, String charEncoding) {
		if (StringUtil.isBlank(content) || StringUtil.isBlank(password)
				|| StringUtil.isBlank(sIV) || StringUtil.isBlank(charEncoding)) {// 参数任意为空则原样返回
			return content;
		}
		try {
			SecretKey secretKey = new SecretKeySpec(
					password.getBytes(charEncoding), AES_METHOD);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES_METHOD);
			Cipher cipher = Cipher.getInstance(AES_CIPHER);
			IvParameterSpec iv = new IvParameterSpec(sIV.getBytes(charEncoding));// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
			// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
			// 初始化
			byte[] result = cipher.doFinal(Base64.decode(content));
			return new String(result, charEncoding);
			// 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
 

    public static void main(String[] args) throws Exception {
        String data = "AES数据";
        String key = "FZG4XCMSTTXU9NBS";
        String iv = "D1EW3JSQLCH2DWKV";
        System.out.println("加密前数据: string:" + "AES数据");
        System.out.println();
        
        String encryptStr = aesEncrypt(data, key, iv);
        System.out.println("加密后数据: hexStr:" + encryptStr);
        System.out.println();
        
        String decryptStr = aesDecrypt(encryptStr, key,iv);
        System.out.println("解密后数据: hexStr:" + decryptStr);
        System.out.println();
        
    }
}
