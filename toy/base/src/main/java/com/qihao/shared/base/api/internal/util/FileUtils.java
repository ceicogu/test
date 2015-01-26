/*
 * Copyright 2014 Aliyun.com All right reserved. This software is the
 * confidential and proprietary information of Aliyun.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Aliyun.com .
 */
package com.qihao.shared.base.api.internal.util;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.LinkedList;
import java.util.List;

/**
 * 类File.java的实现描述：TODO 类实现描述
 * 
 * @author zhihua.zhangzh Mar 14, 2014 1:55:54 PM
 */
public class FileUtils {

    /**
     * @param name
     * @return
     */
    public static String getTempdir(String name) {
        String dir = System.getProperty("java.io.tmpdir") + "/" + name;
        File fileDis = new File(dir);
        fileDis.mkdirs();
        return dir;
    }

    /**
     * 获取目录下的所有文件
     * 
     * @param path
     * @return
     */
    public static List<String> getFilenames(String path) {
        List<String> list = new LinkedList<String>();
        File dir = new File(path);
        File file[] = dir.listFiles();
        for (int i = 0; i < file.length; i++) {
            if (!file[i].isDirectory()) {
                list.add(file[i].getName());
            }
        }
        return list;
    }

    /**
     * 写文件
     * 
     * @param filename
     * @param data
     */
    static boolean _write(String filename, String data, boolean isOverwrite) {
        File file = new File(filename);
        RandomAccessFile raf = null;
        FileChannel fc = null;
        FileLock fl = null;

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            raf = new RandomAccessFile(file, "rw");
            fc = raf.getChannel();
            fl = fc.tryLock();
            if (isOverwrite) {
                raf.seek(0);
            } else {
                raf.seek(raf.length());
            }
            raf.write(data.getBytes());
            //out.write(data.getBytes("utf-8"));
            
            return true;
        } catch (Exception e) {
            return false;
        } finally{
        	try{
	        	if(null != fl) fl.release();  
        	}
        	catch(Exception ex) {
        		ex.printStackTrace();
        	}          
        	try{
	        	if(null != fc)   fc.close();
        	}
        	catch(Exception ex) {
        		ex.printStackTrace();
        	}   
        	try{
	            if(null != raf) raf.close();
	            raf = null;        
        	}
        	catch(Exception ex) {
        		ex.printStackTrace();
        	}           	
        }
    }

    /**
     * 写文件
     * 
     * @param filename
     * @param data
     */
    public static boolean write(String filename, String data) {
        return _write(filename, data, false);
    }

    /**
     * 写文件
     * 
     * @param filename
     * @param data
     */
    public static boolean overwrite(String filename, String data) {
        return _write(filename, data, true);
    }

    /**
     * 读取文件
     * 
     * @param filename
     * @return
     */
    public static String read(String filename) {
        return read(filename, 0);
    }

    /**
     * 读取文件
     * 
     * @param filename
     * @param filePointer
     * @return
     */
    public static String read(String filename, long filePointer) {
        FileUtilsReadResult rr = _read(filename, filePointer);
        return rr.getContent();
    }

    /**
     * 读取文件
     * 
     * @param filename
     * @param filePointer
     * @return
     */
    public static FileUtilsReadResult _read(String filename, long filePointer) {
        try {
            FileUtilsReadResult rr = new FileUtilsReadResult();
            File file = new File(filename);
            RandomAccessFile fis = new RandomAccessFile(file, "r");

            if (filePointer > 0) {
                fis.seek(filePointer);
            }
            byte[] buf = new byte[1024];
            int len = 0;
            StringBuffer sb = new StringBuffer();
            while ((len = fis.read(buf)) != -1) {
                String nb = new String(buf);
                sb.append(nb);
                filePointer += len;
                buf = new byte[1024];
            }
            fis.close();
            fis = null;

            rr.setFilePointer(filePointer);
            rr.setContent(sb.toString().trim());
            return rr;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean checkFileExists(String filename) {
        try {
            File file = new File(filename);
            return file.exists();
            //return true;
        } catch (Exception e) {
            return false;
        }
    }
}
