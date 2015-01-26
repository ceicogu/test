package com.qihao.shared.base.api;

import java.util.Map;

/**
 * 上传请求接口，支持同时上传多个文件。
 */
public interface GeneralUploadRequest<T extends GeneralResponse> extends GeneralRequest<T> {

    /**
     * 获取所有的Key-Value形式的文件请求参数集合。其中：
     * <ul>
     * <li>Key: 请求参数名</li>
     * <li>Value: 请求参数文件元数据</li>
     * </ul>
     * 
     * @return 文件请求参数集合
     */
    public Map<String, FileItem> getFileParams();

}
