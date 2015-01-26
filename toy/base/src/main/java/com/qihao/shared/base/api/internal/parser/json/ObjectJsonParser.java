package com.qihao.shared.base.api.internal.parser.json;


import com.alibaba.fastjson.JSON;
import com.qihao.shared.base.api.GeneralApiException;
import com.qihao.shared.base.api.GeneralParser;
import com.qihao.shared.base.api.GeneralResponse;

/**
 * 单个JSON对象解释器。
 */
public class ObjectJsonParser<T extends GeneralResponse> implements GeneralParser<T> {

    private Class<T> clazz;

    public ObjectJsonParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T parse(String rsp) throws GeneralApiException {
        return JSON.parseObject(rsp, clazz);
        /*
         * Converter converter = new JsonConverter(); return
         * converter.toResponse(rsp, clazz);
         */
    }

    public Class<T> getResponseClass() {
        return clazz;
    }

}
