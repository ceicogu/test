/*
 * Copyright 2013 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.shared.base.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Hex;

/**
 * 通过DES加密解密实现一个String字符串的加密和解密.
 * 
 * @author kaowp
 */
public class DESCoder {
    private static String charsetName = "UTF-8";

    /**
     * @return DES算法密钥
     */
    public static byte[] generateKey() {
        try {

            // DESCoder算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 生成一个DESCoder算法的KeyGenerator对象
            KeyGenerator kg = KeyGenerator.getInstance("DESCoder");
            kg.init(sr);

            // 生成密钥
            SecretKey secretKey = kg.generateKey();

            // 获取密钥数据
            byte[] key = secretKey.getEncoded();

            return key;
        } catch (NoSuchAlgorithmException e) {
            System.err.println("DESCoder算法，生成密钥出错!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加密函数
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return 返回加密后的数据
     */
    public static byte[] encrypt(byte[] data, String keyStr) {

        try {
            byte[] key = keyStr.getBytes(charsetName);
            // DESCoder算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESCoderKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DESCoder in ECB mode
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，加密数据出错!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解密函数
     * 
     * @param data 解密数据
     * @param key 密钥
     * @return 返回解密后的数据
     */
    public static byte[] decrypt(byte[] data, String keyStr) {
        try {
            byte[] key = keyStr.getBytes(charsetName);
            // DESCoder算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // byte rawKeyData[] = /* 用某种方法获取原始密匙数据 */;

            // 从原始密匙数据创建一个DESKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESCoderKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DESCoder in ECB mode
            Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secretKey, sr);

            // 正式执行解密操作
            byte decryptedData[] = cipher.doFinal(data);

            return decryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，解密出错。");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 加密函数
     * 
     * @param data 加密数据
     * @param key 密钥
     * @return 返回加密后的数据
     */
    public static byte[] CBCEncrypt(byte[] data, byte[] key, byte[] iv) {

        try {
            // 从原始密钥数据创建DESrKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESCoderKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // 若采用NoPadding模式，data长度必须是8的倍数
            // Cipher cipher = Cipher.getInstance("DESCoder/CBC/NoPadding");

            // 用密匙初始化Cipher对象
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, param);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(data);

            return encryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，加密数据出错!");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 解密函数
     * 
     * @param data 解密数据
     * @param key 密钥
     * @return 返回解密后的数据
     */
    public static byte[] CBCDecrypt(byte[] data, byte[] key, byte[] iv) {
        try {
            // 从原始密匙数据创建一个DESCoderKeySpec对象
            DESKeySpec dks = new DESKeySpec(key);

            // 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DESCoder in CBC mode
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            // 若采用NoPadding模式，data长度必须是8的倍数
            // Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");

            // 用密匙初始化Cipher对象
            IvParameterSpec param = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, param);

            // 正式执行解密操作
            byte decryptedData[] = cipher.doFinal(data);

            return decryptedData;
        } catch (Exception e) {
            System.err.println("DES算法，解密出错。");
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        try {
            String key = "FZG4XCMS";//TTXU9NBS
            byte[] iv = "D1EW3JSQ".getBytes(charsetName);//LCH2DWKV
            String dataContent = "DES数据";

            System.out.println("加密前数据: string:" + dataContent);
            System.out.println();
            byte[] encryptData = DESCoder.CBCEncrypt(dataContent.getBytes(charsetName), key.getBytes(charsetName), iv);
            System.out.println("加密后数据: hexStr:" + Hex.encodeHexString(encryptData));
            System.out.println();
            byte[] decryptData = DESCoder.CBCDecrypt(encryptData, key.getBytes(charsetName), iv);
            System.out.println("解密后数据: string:" + new String(decryptData, charsetName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}