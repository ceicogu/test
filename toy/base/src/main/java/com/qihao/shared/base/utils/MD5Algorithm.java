package com.qihao.shared.base.utils;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class MD5Algorithm {
	private static final String MD5_ALGORITHM = "MD5";
    /**
     * 签名
     * @param plainData
     * @param secureKey 对应的密钥 ,十六进制
     * @return
     * @throws Exception
     */
    public static String digest(String plainData, String secureKey) throws Exception {
        MessageDigest algorithm = MessageDigest.getInstance(MD5_ALGORITHM);
        byte[] plainBytes = plainData.getBytes("UTF-8");
        char[] aa = secureKey.toCharArray();
        byte[] skeyHexBytes = Hex.decodeHex(aa);
      
        algorithm.update(plainBytes);
        algorithm.update(skeyHexBytes);

        byte[] bytes = Base64.encodeBase64(algorithm.digest());
        return new String(bytes, "UTF-8");
    }

    public static void main(String[] args) throws Exception {
        String data = "PlainText";
        String secureKey = "1f8b638841b06b291d41";
        System.out.println(MD5Algorithm.digest(data,secureKey));
    }
}
