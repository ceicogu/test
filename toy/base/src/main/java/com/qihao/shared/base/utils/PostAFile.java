package com.qihao.shared.base.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
/**
 * http://www.theserverside.com/news/1365153/HttpClient-and-FileUpload
 * @author luqiao
 *
 */
public class PostAFile {
    private static String url =
    	      "http://182.92.162.223/api/resource/upload.json";

    	    public static void main(String[] args) throws IOException {
    	        HttpClient client = new HttpClient();
    	        MultipartPostMethod mPost = new MultipartPostMethod(url);
    	        client.setConnectionTimeout(8000);

    	        // Send any XML file as the body of the POST request
    	        File f1 = new File("/Users/luqiao/tmp/4.jpg");
    	        File f2 = new File("/Users/luqiao/tmp/5.jpg");
    	        File f3 = new File("/Users/luqiao/tmp/6.jpg");

    	        System.out.println("File1 Length = " + f1.length());
    	        System.out.println("File2 Length = " + f2.length());
    	        System.out.println("File3 Length = " + f3.length());
    	        mPost.addParameter("authToken", "qUxgh9eoZ/MQUVvcM6PJlgbSBnPJwljJ4t7BYMNwWUc=");
    	        mPost.addParameter(f1.getName(), f1);
    	        mPost.addParameter(f2.getName(), f2);
    	        mPost.addParameter(f3.getName(), f3);

    	        int statusCode1 = client.executeMethod(mPost);

    	        System.out.println("statusLine>>>" + mPost.getStatusLine());
    	        mPost.releaseConnection();
    	    }
}
