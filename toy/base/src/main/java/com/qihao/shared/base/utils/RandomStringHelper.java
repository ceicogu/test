/*
 * Copyright 2013 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.shared.base.utils;

import java.util.Random;

/**
 * 类RandomStringHelper.java的实现描述：TODO 类实现描述 
 * @author kaowp 2013-3-7 下午3:14:51
 */
public class RandomStringHelper {
    private static final String BASE_STRING = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String BASE_NUM = "0123456789";
    /**
     * 生成指定长度的随机字符串
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(BASE_STRING.length());
            sb.append(BASE_STRING.charAt(number));
        }
        return sb.toString();
    }
    public static String getRandomNum(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(BASE_NUM.length());
            sb.append(BASE_NUM.charAt(number));
        }
        return sb.toString();
    }
}
