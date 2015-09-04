package com.qihao.toy.biz.solr;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class SolrObject {
    public static  Object toBean( SolrDocument record , Class clazz){
        
        Object o = null;
       try {
           o = clazz.newInstance();
       } catch (InstantiationException e1) {
           // TODO Auto-generated catch block
           e1.printStackTrace();
       } catch (IllegalAccessException e1) {
           // TODO Auto-generated catch block
           e1.printStackTrace();
       }
        Field[] fields =   clazz.getDeclaredFields();
        for(Field field:fields){
            Object value = record.get(field.getName());
            try {
               BeanUtils.setProperty(o, field.getName(), value);
           } catch (IllegalAccessException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           } catch (InvocationTargetException e) {
               // TODO Auto-generated catch block
               e.printStackTrace();
           }
        }
       return o;
   }
    
   public static Object toBeanList(SolrDocumentList records, Class  clazz){
       List  list = new ArrayList();
       for(SolrDocument record : records){
           list.add(toBean(record,clazz));
       }
       return list;
   }
}
