package com.qihao.shared.base.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClazzFactory {
    private static final Log logger = LogFactory.getLog(ClazzFactory.class);

    /**
     * 利用递归找一个类的指定方法，如果找不到，去父亲里面找直到最上层Object对象为止。
     * 
     * @param clazz 目标类
     * @param methodName 方法名
     * @param classes 方法参数类型数组
     * @return 方法对象
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Method getMethod(Class clazz, String methodName, final Class[] classes) throws Exception {
        Method method = null;
        try {
            method = clazz.getDeclaredMethod(methodName, classes);
        } catch (NoSuchMethodException e) {
            try {
                method = clazz.getMethod(methodName, classes);
            } catch (NoSuchMethodException ex) {
                if (clazz.getSuperclass() == null) {
                    return method;
                } else {
                    method = getMethod(clazz.getSuperclass(), methodName, classes);
                }
            }
        }
        return method;
    }

    /**
     * @param obj 调整方法的对象
     * @param methodName 方法名
     * @param classes 参数类型数组
     * @param objects 参数数组
     * @return 方法的返回值
     */
    @SuppressWarnings("rawtypes")
    public static Object invoke(final Object obj, final String methodName, final Class[] classes, final Object[] objects) {
        try {
            Method method = getMethod(obj.getClass(), methodName, classes);
            method.setAccessible(true);// 调用private方法的关键一句话
            return method.invoke(obj, objects);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Object invoke(final Object obj, final String methodName, final Class[] classes) {
        return invoke(obj, methodName, classes, new Object[] {});
    }

    public static Object invoke(final Object obj, final String methodName) {
        return invoke(obj, methodName, new Class[] {}, new Object[] {});
    }

    /**
     * 自动setter，会通过反射的方式将string类型的值自动转换
     * 
     * @param obj
     * @param config
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean setter(Object obj, Map<String, Object> config) {
        if (config == null) {
            throw new IllegalArgumentException("BeanFactory.setter.config is null");
        }
        Class<? extends Object> clazz = obj.getClass();
        logger.debug(clazz.getName() + ", setter:" + config.toString());
        Iterator<Entry<String, Object>> iter = config.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            String fieldName = entry.getKey().toString();
            String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            String setMethodName = "set" + methodName;
            String getMethodName = "get" + methodName;

            try {
                Method method = getMethod(clazz, getMethodName, null);
                Object value = null;
                if (entry.getValue().getClass().getSimpleName().equalsIgnoreCase("string")) {
                    String fieldType = method.getReturnType().getSimpleName();
                    String val = entry.getValue().toString();
                    if (fieldType.equalsIgnoreCase("boolean")) {
                        value = Boolean.valueOf(val);
                    } else if (fieldType.equalsIgnoreCase("byte")) {
                        value = Byte.valueOf(val);
                    } else if (fieldType.equalsIgnoreCase("char") || fieldType.equalsIgnoreCase("Character")) {
                        value = val.charAt(0);
                    } else if (fieldType.equalsIgnoreCase("short")) {
                        value = Short.valueOf(val);
                    } else if (fieldType.equalsIgnoreCase("int") || fieldType.equalsIgnoreCase("Integer")) {
                        value = Integer.valueOf(val);
                    } else if (fieldType.equalsIgnoreCase("long")) {
                        value = Long.valueOf(val);
                    } else if (fieldType.equalsIgnoreCase("float")) {
                        value = Float.valueOf(val);
                    } else if (fieldType.equalsIgnoreCase("double")) {
                        value = Double.valueOf(val);
                    } else {// Not in the Java the eight basic type inside
                        logger.debug("Not in the Java the eight basic type inside:" + fieldType);
                        value = entry.getValue();
                    }
                }
                clazz.getMethod(setMethodName, method.getReturnType()).invoke(obj, value);
            } catch (Exception e) {
                logger.warn("fieldName:[" + fieldName + "] Setter error: " + e.getMessage());
            }
        }
        return true;
    }

    /**
     * 获取某个类中的所有getX方法的值
     * 
     * @param o 目标对象
     * @param prefix 给每个值都加一个前缀
     * @return
     */
    public static Map<String, Object> getter(Object o, String prefix) {
        Map<String, Object> map = new HashMap<String, Object>();
        Class<? extends Object> clazz = o.getClass();
        logger.debug("getter via class:" + clazz.getName());
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().startsWith("get")) {// getXX()
                // methods[i].setAccessible(true);// 允许private被访问
                try {
                    String name = methods[i].getName().substring(3);
                    String fieldName = name.substring(0, 1).toLowerCase() + name.substring(1);

                    Object object = methods[i].invoke(o);
                    logger.debug(fieldName + "=" + object.toString());
                    map.put(prefix + fieldName, object);
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
        }
        return map;
    }

    /**
     * 获取某个类中的所有getX方法的值
     * 
     * @param o 目标对象
     * @return
     */
    public static Map<String, Object> getter(Object o) {
        return getter(o, "");
    }
}
