/**
 * Aliyun.com Inc.
 * Copyright (c) 2012-2013 All Rights Reserved.
 */
package com.qihao.shared.base.api;

/**
 * @author seno.gu
 */
public class GeneralConstants {

    public static final String FORMAT                          = "format";

    public static final String VERSION                         = "version";

    public static final String ACCESS_KEY_ID                   = "AccessKeyId";

    public static final String SIGN                            = "Signature";

    public static final String SIGN_TYPE                       = "SignatureMethod";

    public static final String SIGN_TYPE_RSA                   = "RSA";

    public static final String SIGN_ALGORITHMS                 = "SHA1WithRSA";

    public static final String SIGN_VERSION                    = "SignatureVersion";

    public static final String SIGN_NONCE                      = "SignatureNonce";

    public static final String TIMESTAMP                       = "Timestamp";

    public static final String METHOD                          = "Action";

    /** 默认时间格式 **/
    public static final String DATE_TIME_FORMAT                = "yyyy-MM-dd HH:mm:ss";

    /** Date默认时区 **/
    public static final String DATE_TIMEZONE                   = "GMT+8";

    /** UTF-8字符集 **/
    public static final String CHARSET_UTF8                    = "UTF-8";

    /** GBK字符集 **/
    public static final String CHARSET_GBK                     = "GBK";

    /** JSON 应格式 */
    public static final String FORMAT_JSON                     = "json";

    /** XML 应格式 */
    public static final String FORMAT_XML                      = "xml";

    /** HTTP-Client链接超时时长500毫秒 */
    public static final int    HTTP_CLIENT_CONNECTION_TIME_OUT = 500;

    /** HTTP-Client请求超时时长1000毫秒 */
    public static final int    HTTP_CLIENT_TIME_OUT            = 1000;
}
