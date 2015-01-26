/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.shared.base.api.internal.util;

import java.io.Serializable;

/**
 * 类FileUtilsReadResult.java的实现描述：TODO 类实现描述
 * 
 * @author zhihua.zhangzh Mar 14, 2014 1:55:54 PM
 */
public class FileUtilsReadResult implements Serializable {
    private static final long serialVersionUID = -6996902663761840770L;
    long                      filePointer;
    String                    content;

    public void setFilePointer(long filePointer) {
        this.filePointer = filePointer;
    }

    public long getFilePointer() {
        return this.filePointer;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }
}
