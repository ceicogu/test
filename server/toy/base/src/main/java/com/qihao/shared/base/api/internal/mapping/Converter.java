package com.qihao.shared.base.api.internal.mapping;

import com.qihao.shared.base.api.GeneralApiException;
import com.qihao.shared.base.api.GeneralResponse;

/**
 * 动态格式转换器。
 * 
 * @author seno.gu
 * @since 1.0, Apr 11, 2010
 */
public interface Converter {

    /**
     * 把字符串转换为响应对象。
     * 
     * @param <T> 领域泛型
     * @param rsp 响应字符串
     * @param clazz 领域类型
     * @return 响应对象
     * @throws TopException
     */
    public <T extends GeneralResponse> T toResponse(String rsp, Class<T> clazz) throws GeneralApiException;

}
