package com.qihao.shared.base.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import com.qihao.shared.base.DescedEnum;

/**
 * @author wb-kouguangyi
 * @since 2012-02-21
 */
public class DescedEnumUtils {

    public static <T extends DescedEnum> T valueOf(Class<T> enumType,int value) {
        for (T i : enumType.getEnumConstants()) {
            if (i.numberValue() == value) {
                return i;
            }
        }
        return null;
    }

    public static <T extends DescedEnum> String numberToDesc(int n, Class<T> clz) {
        T e = valueOf(clz,n);
        if (e != null) {
            return e.desc();
        }

        return null;
    }

    public static <T extends DescedEnum> Map<String, T> getMap(Class<T> clz) {

        Map<String, T> lm = new LinkedHashMap<String, T>();
        for (T t : clz.getEnumConstants()) {
            lm.put(t.toString(), t);
        }
        return lm;
    }

    public static <T extends DescedEnum> boolean isSpecEnum(T t, String enumString) {
        if (t == null) return false;
        return t.toString().equals(enumString);
    }
    public static <T extends DescedEnum> Map<Integer, String> getN2SMap(Class<T> clz) {
        Map<Integer, String> lm = new LinkedHashMap<Integer, String>();
        for (T t : clz.getEnumConstants()) {
            lm.put(t.numberValue(), t.desc());
        }
        return lm;
    }
}
