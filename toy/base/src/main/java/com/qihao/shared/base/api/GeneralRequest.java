package com.qihao.shared.base.api;

import java.util.Map;

/**
 * 请求接口。
 */
public interface GeneralRequest<T extends GeneralResponse> {

    /**
     * 获取API名称。
     * 
     * @return API名称
     */
    public String getApiMethodName();

    /**
     * 获取所有的Key-Value形式的文本请求参数集合。其中：
     * <ul>
     * <li>Key: 请求参数名</li>
     * <li>Value: 请求参数值</li>
     * </ul>
     * 
     * @return 文本请求参数集合
     */
    public Map<String, String> getTextParams();

    /**
     * 得到当前接口的版本
     * 
     * @return API版本
     */
    public String getApiVersion();

    /**
     * 设置当前API的版本信息
     * 
     * @param apiVersion API版本
     */
    public void setApiVersion(String apiVersion);

    /**
     * 得到当前API的响应结果类型
     * 
     * @return 响应类型
     */
    public Class<T> getResponseClass();

}
