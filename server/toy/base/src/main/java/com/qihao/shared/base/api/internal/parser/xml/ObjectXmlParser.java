package com.qihao.shared.base.api.internal.parser.xml;

import com.qihao.shared.base.api.GeneralApiException;
import com.qihao.shared.base.api.GeneralParser;
import com.qihao.shared.base.api.GeneralResponse;
import com.qihao.shared.base.api.internal.mapping.Converter;


/**
 * 单个JSON对象解释器。
 * 
 * @author seno.gu
 * @since 1.0, Apr 11, 2010
 */
public class ObjectXmlParser<T extends GeneralResponse> implements GeneralParser<T> {

    private Class<T> clazz;

    public ObjectXmlParser(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T parse(String rsp) throws GeneralApiException {
        Converter converter = new XmlConverter();
        return converter.toResponse(rsp, clazz);
    }

    public Class<T> getResponseClass() {
        return clazz;
    }

}
