/**
 * Aliyun.com Inc.
 * Copyright (c) 2012-2013 All Rights Reserved.
 */
package com.qihao.shared.base.api;

/**
 * @author seno.gu
 */
public interface GeneralClient {

    /**
     * @param <T>
     * @param request
     * @return
     * @throws GeneralApiException
     */
    public <T extends GeneralResponse> T execute(GeneralRequest<T> request) throws GeneralApiException;

    /**
     * @param <T>
     * @param request
     * @param accessToken
     * @return
     * @throws GeneralApiException
     */
    public <T extends GeneralResponse> T execute(GeneralRequest<T> request, String authToken) throws GeneralApiException;

}
