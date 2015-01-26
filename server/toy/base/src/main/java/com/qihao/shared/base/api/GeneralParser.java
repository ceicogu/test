package com.qihao.shared.base.api;

/**
 * 响应解释器接口。响应格式可以是JSON, XML等等。
 */
public interface GeneralParser<T extends GeneralResponse> {

    /**
     * 把响应字符串解释成相应的领域对象。
     * 
     * @param rsp 响应字符串
     * @return 领域对象
     */
    public T parse(String rsp) throws GeneralApiException;

    /**
     * 获取响应类类型。
     */
    public Class<T> getResponseClass() throws GeneralApiException;

}
