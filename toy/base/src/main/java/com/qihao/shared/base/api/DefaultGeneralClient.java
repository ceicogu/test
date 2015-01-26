package com.qihao.shared.base.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.qihao.shared.base.api.internal.parser.json.ObjectJsonParser;
import com.qihao.shared.base.api.internal.util.*;


public class DefaultGeneralClient implements GeneralClient {

    private String serverUrl;
    private String accessKeyId;
    private String privateKey;
    private String format         = GeneralConstants.FORMAT_JSON;
    private String sign_type      = GeneralConstants.SIGN_TYPE_RSA;

    private int    connectTimeout = GeneralConstants.HTTP_CLIENT_CONNECTION_TIME_OUT;
    private int    readTimeout    = GeneralConstants.HTTP_CLIENT_TIME_OUT;

    public DefaultGeneralClient(String serverUrl, String accessKeyId, String privateKey) {
        this.serverUrl = serverUrl;
        this.accessKeyId = accessKeyId;
        this.privateKey = privateKey;
    }

    public DefaultGeneralClient(String serverUrl, String accessKeyId, String privateKey, String format) {
        this(serverUrl, accessKeyId, privateKey);
        this.format = format;
    }

    public <T extends GeneralResponse> T execute(GeneralRequest<T> request) throws GeneralApiException {
        return execute(request, null);
    }

    public <T extends GeneralResponse> T execute(GeneralRequest<T> request, String accessToken) throws GeneralApiException {

        GeneralParser<T> parser = null;
        parser = new ObjectJsonParser<T>(request.getResponseClass());
        /*
         * if (AliyunConstants.FORMAT_XML.equals(this.format)) { parser = new
         * ObjectXmlParser<T>(request.getResponseClass()); } else { parser = new
         * ObjectJsonParser<T>(request.getResponseClass()); }
         */

        return _execute(request, parser, accessToken);
    }

    private <T extends GeneralResponse> T _execute(GeneralRequest<T> request, GeneralParser<T> parser, String authToken)
            throws GeneralApiException {
        //Map<String, Object> rt = doPost(request, authToken);
        Map<String, Object> rt = doGet(request, authToken);
        if (rt == null) {
            return null;
        }

        T tRsp = null;
        try {
            tRsp = parser.parse((String) rt.get("rsp"));
            tRsp.setBody((String) rt.get("rsp"));

        } catch (RuntimeException e) {
            GeneralLogger.logBizError((String) rt.get("rsp"));
            throw e;
        } catch (GeneralApiException e) {
            GeneralLogger.logBizError((String) rt.get("rsp"));
            throw new GeneralApiException(e);
        }

        tRsp.setParams((GeneralHashMap) rt.get("textParams"));
        if (!tRsp.isSuccess()) {
            GeneralLogger.logErrorScene(rt, tRsp, "");
        }
        return tRsp;
    }

    public <T extends GeneralResponse> Map<String, Object> doPost(GeneralRequest<T> request, String accessToken)
            throws GeneralApiException {
        Map<String, Object> result = new HashMap<String, Object>();
        RequestParametersHolder requestHolder = new RequestParametersHolder();
        GeneralHashMap appParams = new GeneralHashMap(request.getTextParams());
        requestHolder.setApplicationParams(appParams);

        GeneralHashMap protocalMustParams = new GeneralHashMap();
        protocalMustParams.put(GeneralConstants.METHOD, request.getApiMethodName());
        /*
         * protocalMustParams.put(AliyunConstants.VERSION,
         * request.getApiVersion());
         * protocalMustParams.put(AliyunConstants.ACCESS_KEY_ID,
         * this.accessKeyId); protocalMustParams.put(AliyunConstants.SIGN_TYPE,
         * this.sign_type);
         */
        Long timestamp = System.currentTimeMillis();
        /*
         * DateFormat df = new
         * SimpleDateFormat(AliyunConstants.DATE_TIME_FORMAT);
         * df.setTimeZone(TimeZone.getTimeZone(AliyunConstants.DATE_TIMEZONE));
         * protocalMustParams.put(AliyunConstants.TIMESTAMP,df.format(new
         * Date(timestamp)));
         */requestHolder.setProtocalMustParams(protocalMustParams);

        GeneralHashMap protocalOptParams = new GeneralHashMap();
        //		protocalOptParams.put(AliyunConstants.FORMAT, format);
        requestHolder.setProtocalOptParams(protocalOptParams);

        StringBuffer urlSb = new StringBuffer(serverUrl);
        try {
            String sysMustQuery = WebUtils.buildQuery(requestHolder.getProtocalMustParams(),
                    GeneralConstants.CHARSET_UTF8);
            String sysOptQuery = WebUtils
                    .buildQuery(requestHolder.getProtocalOptParams(), GeneralConstants.CHARSET_UTF8);

            urlSb.append("?");
            urlSb.append(sysMustQuery);
            if (sysOptQuery != null & sysOptQuery.length() > 0) {
                urlSb.append("&");
                urlSb.append(sysOptQuery);
            }
        } catch (IOException e) {
            throw new GeneralApiException(e);
        }

        String rsp = null;
        try {
            if (request instanceof GeneralUploadRequest) {
                GeneralUploadRequest<T> uRequest = (GeneralUploadRequest<T>) request;
                Map<String, FileItem> fileParams = GeneralUtils.cleanupMap(uRequest.getFileParams());
                rsp = WebUtils.doPost(urlSb.toString(), appParams, fileParams, connectTimeout, readTimeout);
            } else {
                rsp = WebUtils.doPost(urlSb.toString(), appParams, connectTimeout, readTimeout);
            }
        } catch (IOException e) {
            throw new GeneralApiException(e);
        }
        result.put("rsp", rsp);
        result.put("textParams", appParams);
        result.put("protocalMustParams", protocalMustParams);
        result.put("protocalOptParams", protocalOptParams);
        result.put("url", urlSb.toString());
        return result;
    }

    public <T extends GeneralResponse> Map<String, Object> doGet(GeneralRequest<T> request, String accessToken)
            throws GeneralApiException {
        Map<String, Object> result = new HashMap<String, Object>();
        RequestParametersHolder requestHolder = new RequestParametersHolder();
        GeneralHashMap appParams = new GeneralHashMap(request.getTextParams());
        requestHolder.setApplicationParams(appParams);

        GeneralHashMap protocalMustParams = new GeneralHashMap();
        protocalMustParams.put(GeneralConstants.METHOD, request.getApiMethodName());
        /*
         * protocalMustParams .put(AliyunConstants.VERSION,
         * request.getApiVersion());
         * protocalMustParams.put(AliyunConstants.ACCESS_KEY_ID,
         * this.accessKeyId); protocalMustParams.put(AliyunConstants.SIGN_TYPE,
         * this.sign_type);
         */

        Long timestamp = System.currentTimeMillis();
        /*
         * DateFormat df = new
         * SimpleDateFormat(AliyunConstants.DATE_TIME_FORMAT);
         * df.setTimeZone(TimeZone.getTimeZone(AliyunConstants.DATE_TIMEZONE));
         * protocalMustParams.put(AliyunConstants.TIMESTAMP, df.format(new
         * Date(timestamp)));
         */
        requestHolder.setProtocalMustParams(protocalMustParams);

        GeneralHashMap protocalOptParams = new GeneralHashMap();
        //		protocalOptParams.put(AliyunConstants.FORMAT, format);
        requestHolder.setProtocalOptParams(protocalOptParams);

        StringBuffer urlSb = new StringBuffer(serverUrl);
        try {
            String sysMustQuery = WebUtils.buildQuery(requestHolder.getProtocalMustParams(),
                    GeneralConstants.CHARSET_UTF8);
            String sysOptQuery = WebUtils
                    .buildQuery(requestHolder.getProtocalOptParams(), GeneralConstants.CHARSET_UTF8);

            urlSb.append("?");
            urlSb.append(sysMustQuery);
            /*
             * if (sysOptQuery != null & sysOptQuery.length() > 0) {
             * urlSb.append("&"); urlSb.append(sysOptQuery); }
             */
        } catch (IOException e) {
            throw new GeneralApiException(e);
        }

        String rsp = null;
        try {
            rsp = WebUtils.doGet(urlSb.toString(), appParams, connectTimeout, readTimeout);
        } catch (IOException e) {
            throw new GeneralApiException(e);
        }
        result.put("rsp", rsp);
        result.put("textParams", appParams);
        result.put("protocalMustParams", protocalMustParams);
        result.put("protocalOptParams", protocalOptParams);
        result.put("url", urlSb.toString());
        return result;
    }

}
