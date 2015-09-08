package com.qihao.toy.biz.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

import com.thoughtworks.xstream.XStream;

public class XmlUtils {
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws UnsupportedEncodingException,
			DocumentException {
		Map<String,Object> testMap = new HashMap<String,Object>();
		Map<String,Object> parent = new HashMap<String,Object>();
		Map<String,Object> mother = new HashMap<String,Object>();
		Map<String,Object> child1 = new HashMap<String,Object>();
		Map<String,Object> child2 = new HashMap<String,Object>();
		List<Map<String,Object>> ls = new ArrayList<Map<String,Object>>();
		
		child1.put("name", "大宝");
		child1.put("age", 15);
		child1.put("sex", "男");
		child2.put("name", "二宝");
		child2.put("age", 10);
		child2.put("sex", "女");	
		parent.put("child1", child1);
		parent.put("child2", child2);
		parent.put("name", "爸爸");
		parent.put("age", 40);
		mother.put("name", "妈妈");
		mother.put("age", 38);
		testMap.put("parent", parent);
		testMap.put("mother", mother);
		testMap.put("num", 2);
//		ls.add(child1);
//		ls.add(child2);
//		testMap.put("child", ls);
		String aa1 = map2xml(testMap, "request");
		Map<String, Object> bb1 = xml2map(aa1);
		
		String xmlTxt = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><error>0</error><message>错误信息</message><data><name>dfdfd</name></data></response>";
		// 调用核心入口方法
		Map<String, Object> b1 = xml2map(xmlTxt);

		String a2a = map2xml(b1, "request");

		 b1 = xml2map(a2a);
		System.out.print(b1.toString());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map xml2map(String xmlString) throws DocumentException {
		Document doc = DocumentHelper.parseText(xmlString);
		Element rootElement = doc.getRootElement();

		Map<String, Object> map = (Map<String, Object>) xml2map(rootElement);
		// System.out.println(JSONObject.toJSONString(map));

		return map;
	}

	/***
	 * 核心方法，里面有递归调用
	 * 
	 * @param map
	 * @param ele
	 */
	@SuppressWarnings("unchecked")
	private static Object xml2map(Element element) {
		System.out.println(element);
		Map<String, Object> map = new HashMap<String, Object>();
		List<Element> elements = element.elements();
		if (elements.size() == 0) {
			map.put(element.getName(), element.getText().trim());
			if (!element.isRootElement()) {
				return element.getText();
			}
		} else if (elements.size() == 1) {
			map.put(elements.get(0).getName(), xml2map(elements.get(0)));
		} else if (elements.size() > 1) {
			// 多个子节点的话就得考虑list的情况了，比如多个子节点有节点名称相同的
			// 构造一个map用来去重
			Map<String, Element> tempMap = new HashMap<String, Element>();
			for (Element ele : elements) {
				tempMap.put(ele.getName(), ele);
			}
			Set<String> keySet = tempMap.keySet();
			for (String string : keySet) {
				Namespace namespace = tempMap.get(string).getNamespace();
				List<Element> elements2 = element.elements(new QName(string,
						namespace));
				// 如果同名的数目大于1则表示要构建list
				if (elements2.size() > 1) {
					List<Object> list = new ArrayList<Object>();
					for (Element ele : elements2) {
						list.add(xml2map(ele));
					}
					map.put(string, list);
				} else {
					// 同名的数量不大于1则直接递归去
					map.put(string, xml2map(elements2.get(0)));
				}
			}
		}

		return map;
	}

	/**
	 * 根据Map组装xml消息体，值对象仅支持基本数据类型、String、BigInteger、BigDecimal，
	 * 以及包含元素为上述支持数据类型的Map
	 * 
	 * @param vo
	 * @param rootElement
	 * @return
	 * @author
	 */
	public static String map2xml(Map<String, Object> vo, String rootElement) {
		Document doc = DocumentHelper.createDocument();
		Element body = DocumentHelper.createElement(rootElement);
		doc.add(body);
		map2xml(body, vo);
		return doc.asXML();
	}

	@SuppressWarnings("unchecked")
	private static void map2xml(Element body, Map<String, Object> vo) {
		if (vo != null) {
			Iterator<String> it = vo.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				if (StringUtils.isNotEmpty(key)) {
					Object obj = vo.get(key);
					Element element = DocumentHelper.createElement(key);
					if (obj != null) {
						if (obj instanceof java.lang.String) {
							element.setText((String) obj);
						} else {
							if (obj instanceof java.lang.Character
									|| obj instanceof java.lang.Boolean
									|| obj instanceof java.lang.Number
									|| obj instanceof java.math.BigInteger
									|| obj instanceof java.math.BigDecimal) {
//								Attribute attr = DocumentHelper
//										.createAttribute(element, "type", obj
//												.getClass().getCanonicalName());
//								element.add(attr);
								element.setText(String.valueOf(obj));
							} else if (obj instanceof java.util.Map) {
//								Attribute attr = DocumentHelper
//										.createAttribute(element, "type",
//												java.util.Map.class
//														.getCanonicalName());
//								element.add(attr);
								map2xml(element, (Map<String, Object>) obj);
							} else {
							}
						}
					}
					body.add(element);
				}
			}
		}
	}
    /** 
     * 将Bean转换为XML 
     * 
     * @param clazzMap 别名-类名映射Map 
     * @param bean     要转换为xml的bean对象 
     * @return XML字符串 
     */ 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static String bean2xml(Map<String, Class> clazzMap, Object bean) { 
        XStream xstream = new XStream(); 
        for (Iterator it = clazzMap.entrySet().iterator(); it.hasNext();) { 
            Map.Entry<String, Class> m = (Map.Entry<String, Class>) it.next(); 
            xstream.alias(m.getKey(), m.getValue()); 
        } 
        String xml = xstream.toXML(bean); 
        return xml; 
    } 

    /** 
     * 将XML转换为Bean 
     * 
     * @param clazzMap 别名-类名映射Map 
     * @param xml      要转换为bean对象的xml字符串 
     * @return Java Bean对象 
     */ 
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object xml2bean(Map<String, Class> clazzMap, String xml) { 
        XStream xstream = new XStream(); 
        for (Iterator it = clazzMap.entrySet().iterator(); it.hasNext();) { 
            Map.Entry<String, Class> m = (Map.Entry<String, Class>) it.next(); 
            xstream.alias(m.getKey(), m.getValue()); 
        } 
        Object bean = xstream.fromXML(xml); 
        return bean; 
    } 

    /** 
     * 获取XStream对象 
     * 
     * @param clazzMap 别名-类名映射Map 
     * @return XStream对象 
     */ 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static XStream getXStreamObject(Map<String, Class> clazzMap) { 
        XStream xstream = new XStream(); 
        for (Iterator it = clazzMap.entrySet().iterator(); it.hasNext();) { 
            Map.Entry<String, Class> m = (Map.Entry<String, Class>) it.next(); 
            xstream.alias(m.getKey(), m.getValue()); 
        } 
        return xstream; 
    } 
    /** 
     * 获取????的XStream对象 
     * 
     * @return XStream对象 
     */ 
//    public static XStream getXXXStream() { 
//        Map<String, Class> cm = new HashMap<String, Class>(); 
//        cm.put("test", org.xxx.Test.class); 
//        cm.put("ssss", org.xxx.s.Ssss.class); 
//        return XMLBeanUtils.getXStreamObject(cm); 
//    } 
}
