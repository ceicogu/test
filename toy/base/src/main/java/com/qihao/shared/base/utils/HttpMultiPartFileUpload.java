package com.qihao.shared.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
/**
 * http://www.theserverside.com/news/1365153/HttpClient-and-FileUpload
 * @author luqiao
 *
 */
public class HttpMultiPartFileUpload {
    private static String url =
    	      "http://182.92.162.223/api/resource/upload.json";

    	    public static void main(String[] args) throws IOException {
    	        HttpClient client = new HttpClient();
    	        PostMethod postMethod = new PostMethod(url);

    	        client.setConnectionTimeout(8000);

    	        // Send any XML file as the body of the POST request
    	        File f = new File("/Users/luqiao/tmp/4.jpg");
    	        System.out.println("File Length = " + f.length());

    	        postMethod.setRequestBody(new FileInputStream(f));
    	        postMethod.setRequestHeader("Content-type",
    	            "text/xml; charset=UTF-8");
    	        postMethod.setParameter("authToken", "qUxgh9eoZ/MQUVvcM6PJlgbSBnPJwljJ4t7BYMNwWUc=");
    	        int statusCode1 = client.executeMethod(postMethod);

    	        System.out.println("statusLine>>>" + postMethod.getStatusLine());
    	        postMethod.releaseConnection();
    	        

    	    }
}
