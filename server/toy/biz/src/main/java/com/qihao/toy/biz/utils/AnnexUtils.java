package com.qihao.toy.biz.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

import com.alibaba.citrus.util.io.StreamUtil;

public class AnnexUtils {
	 private final static String UPLOAD_DIR = "/data/resource/res";
	 
	public static void saveAppFile(FileItem fileItem) throws IOException {
		if (fileItem != null) {
			File destFile = new File(UPLOAD_DIR + "/upload" + "/app" + fileItem.getName());
			InputStream inputStream = null;
			try {
				inputStream = fileItem.getInputStream();
				FileUtils.copyInputStreamToFile(inputStream, destFile);
			} catch (IOException e) {
				e.printStackTrace();
				throw e;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

		}
	}
	 
	public static String saveAnnex(FileItem inFileItem) throws IOException {
		if(null == inFileItem) return null;
		
        String imageFileName = null;
        File uploadDir = null;
        try {
        	uploadDir = new File(UPLOAD_DIR+"/upload");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            if (!uploadDir.isDirectory()) {
                throw new IOException("Could not create directory " + uploadDir.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not get upload directory from ResourceLoader: " + UPLOAD_DIR+"/upload");
        }

        String fileName = inFileItem.getName().replace('\\', '/');
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

        String ext = "";
        int index = fileName.lastIndexOf(".");

        if (index > 0) {
            ext = fileName.substring(index);
        }
        File imageFile = File.createTempFile("annex_", ext, uploadDir);

        imageFileName = imageFile.getName();
        InputStream is = inFileItem.getInputStream();            
        OutputStream os = new BufferedOutputStream(new FileOutputStream(imageFile));

        StreamUtil.io(is, os, true, true);
        return "upload/"+imageFileName;
	}

	public static File getAnnexFile(String fileName) {
        File uploadFile = null;
        try {
        	uploadFile = new File(UPLOAD_DIR+"/"+fileName);
            if (!uploadFile.exists()) {            	
                return null;
            }
            return uploadFile;
        } catch (Exception e) {
            //throw new StoreManagerException("Could not get upload directory from ResourceLoader: " + UPLOAD_DIR);
            return null;
        }
	}
}
