package com.qihao.shared.base.utils;

import com.qihao.shared.base.IntEnum;



/**
 * Created by muchen.lh on 2015/1/20.11:51 类描述：TODO
 */
public class IntEnumUtils {
    public static <T extends IntEnum> T valueOf(Class<T> enumType,int value) {
        for (T i : enumType.getEnumConstants()) {
            if (i.intValue() == value) {
                return i;
            }
        }
        return null;
    }
}
