/*
 * Copyright (c) 2002-2012 Alibaba Group Holding Limited.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qihao.toy.dal.domain;

import com.qihao.shared.base.IntEnum;

import lombok.Getter;
import lombok.Setter;
/**
 * 上传文件信息表
 * @author luqiao
 *
 */
@Setter @Getter
public class UploadDO  extends PageDO {

	private static final long serialVersionUID = 1L;
	public static enum FileType implements IntEnum{
		TEXT(0),//文本文件
		SOUND(1),//语音文件
		VIDEO(2);//视频文件

	    private int    intValue;

	    private FileType(int intValue){
	        this.intValue = intValue;
	    }


		public int intValue() {
			return intValue;
		}
	}
    private Long		uploader;		//文件上传者ID		
    private String		fileName;		//上传文件服务端存储文件名
    private String		fileSuffix;     //上传文件后缀
    private FileType	fileType;		//上传文件类型
    private Integer		duration;		//音频和视频文件播放时长
}
