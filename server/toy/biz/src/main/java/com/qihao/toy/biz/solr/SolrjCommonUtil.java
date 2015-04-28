package com.qihao.toy.biz.solr;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class SolrjCommonUtil {

    public static Map<String, String> getSearchProperty(Object model)  
            throws NoSuchMethodException, IllegalAccessException,  
            IllegalArgumentException, InvocationTargetException {  
        Map<String, String> resultMap = new TreeMap<String, String>();  
        // 获取实体类的所有属性，返回Field数组  
        Field[] field = model.getClass().getDeclaredFields();  
        for (int i = 0; i < field.length; i++) { // 遍历所有属性  
            String name = field[i].getName(); // 获取属性的名字  
            // 获取属性的类型  
            String type = field[i].getGenericType().toString();  
            if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名  
                Method m = model.getClass().getMethod("get" + UpperCaseField(name));  
                String value = (String) m.invoke(model); // 调用getter方法获取属性值  
                if (value != null) {  
                    resultMap.put(name, value);  
                }  
            } else if(type.equals("class java.lang.Long")){
                Method m = model.getClass().getMethod("get" + UpperCaseField(name));  
                Long value = (Long) m.invoke(model); // 调用getter方法获取属性值  
                if (value != null) {  
                    resultMap.put(name, String.valueOf(value));  
                }              	
            }
        }  
        return resultMap;  
    }  
  
    // 转化字段首字母为大写  
    private static String UpperCaseField(String fieldName) {  
        fieldName = fieldName.replaceFirst(fieldName.substring(0, 1), fieldName.substring(0, 1).toUpperCase());  
        return fieldName;  
    }  
}
