package com.qihao.shared.base.utils;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.qihao.shared.base.DataResult;

/**
 * http的client，默认超时三秒
 * @author guangyi.kou
 * @since 2013-10-10
 */
@Slf4j
public class HttpClientUtil {

    /**
     * 默认超时时间
     */
    public static final int     DEFAULT_TIMEOUT        = 3;                                                                // 3秒
    /**
     * 默认编码
     */
    public static final String  DEFAULT_ENCODE         = "utf-8";

    /**
     * post方法
     * @param url
     * @param treeMap
     * @return
     */
    public static DataResult<String> post(String url, TreeMap<String, String> treeMap) {
        return post(url, treeMap, DEFAULT_TIMEOUT);
    }

    /**
     * post方法
     * @param url
     * @param treeMap
     * @param timeoutSecond 超时，秒数
     * @return
     */
    public static DataResult<String> post(String url, TreeMap<String, String> treeMap, int timeoutSecond) {
        return post(url, treeMap, timeoutSecond, DEFAULT_ENCODE);
    }

    /**post方法
     * @param url
     * @param treeMap
     * @param encode
     * @return
     */
    public static DataResult<String> executeMethod(String url, TreeMap<String, String> treeMap, String encode) {
        return post(url, treeMap, DEFAULT_TIMEOUT, encode);
    }

    /**
     * post方法
     * @param url
     * @param timeoutSecond
     * @param encode
     * @return
     */
    public static DataResult<String> post(String url, TreeMap<String, String> treeMap, int timeoutSecond, String encode) {
        PostMethod postMethod = new PostMethod(url);
        postMethod = parsePostData(postMethod, treeMap);

        return executeMethod(postMethod, timeoutSecond, encode);
    }

    /**
     * get方法
     * @param url
     * @return
     */
    public static DataResult<String> get(String url) {
        return get(url, DEFAULT_TIMEOUT);
    }

    /**
     * get方法
     * @param url
     * @param timeoutSecond超时，秒数
     * @return
     */
    public static DataResult<String> get(String url, int timeoutSecond) {
        return get(url, timeoutSecond, DEFAULT_ENCODE);
    }

    /**
     * get方法
     * @param url
     * @param encode
     * @return
     */
    public static DataResult<String> get(String url, String encode) {
        return get(url, DEFAULT_TIMEOUT, encode);
    }

    /**
     *  get方法
     * @param url
     * @param timeoutSecond
     * @param encode
     * @return
     */
    public static DataResult<String> get(String url, int timeoutSecond, String encode) {
        GetMethod getMethod = new GetMethod(url);
        return executeMethod(getMethod, timeoutSecond, encode);
    }

    /**
     * 通用的方法
     * @param httpMethod
     * @return
     */
    public static DataResult<String> executeMethod(HttpMethod httpMethod) {
        return executeMethod(httpMethod, DEFAULT_TIMEOUT);
    }

    /**
     * 通用的方法
     * @param httpMethod
     * @param timeoutSecond
     * @return
     */
    public static DataResult<String> executeMethod(HttpMethod httpMethod, int timeoutSecond) {
        return executeMethod(httpMethod, timeoutSecond, DEFAULT_ENCODE);
    }

    /**
     * 通用的方法
     * @param httpMethod
     * @param encode
     * @return
     */
    public static DataResult<String> executeMethod(HttpMethod httpMethod, String encode) {
        return executeMethod(httpMethod, DEFAULT_TIMEOUT, encode);
    }

    /**
     * 通用的方法
     * @param httpMethod
     * @param timeoutSecond
     * @param encode
     * @return
     */
    public static DataResult<String> executeMethod(HttpMethod httpMethod, int timeoutSecond, String encode) {
        HttpClient httpClient = new HttpClient();
        // 链接超时
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeoutSecond * 1000);
        // 读取超时
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeoutSecond * 1000);
        httpMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + encode);
        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(httpMethod);
        } catch (Exception e) {
            log.error("http链接异常:", e);
        }

        DataResult<String> result = new DataResult<String>();
        if (statusCode == HttpStatus.SC_OK) {
            result.setSuccess(true);
        } else {
            result.setErrorCode(statusCode);
        }

        String resultStr = null;
        try {
            resultStr = httpMethod.getResponseBodyAsString();
        } catch (Exception e) {
            log.error("http得到内容异常:", e);
        }
        httpClient.getHttpConnectionManager().closeIdleConnections(0);
        httpMethod.releaseConnection();
        result.setData(resultStr);
        return result;
    }

    /**
     * 组装post方法
     * @param postMethod
     * @param param
     * @return
     */
    private static PostMethod parsePostData(PostMethod postMethod, TreeMap<String, String> param) {
        Set<Entry<String, String>> entries = param.entrySet();
        Iterator<Entry<String, String>> iter = entries.iterator();

        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();

            postMethod.addParameter(entry.getKey(), entry.getValue());

        }
        return postMethod;
    }
}
