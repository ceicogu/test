package com.qihao.toy.dal.domain.typehandlers;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.collect.Maps;
import com.qihao.shared.base.DescedEnum;
import com.qihao.shared.base.IntEnum;

/**
 * 注册typeHandler用
 * DescedEnum & IntEnum
 */
@Slf4j
public class TypeHandlerRegistry {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Getter
    @Setter
    private String            scanPackage;
    
    private static Map<Class<?>,Class<?>> typeHandlerMap = Maps.newConcurrentMap();
    static {
    	typeHandlerMap.put(IntEnum.class, IntEnumTypeHandler.class);
    	typeHandlerMap.put(DescedEnum.class, DescedEnumTypeHandler.class);
    }
    /**
     * 初始化
     */
    public void init() throws Exception {
        Configuration configuration = sqlSessionFactory.getConfiguration();
        String[] packages = tokenizeToStringArray(this.scanPackage,
                                                  ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);//支持多包
        
        ResolverUtil<Class<?>> resolverUtil = null;
        Set<Class<? extends Class<?>>> enumSet = null;
        for (String enumPackage : packages) {
        	//先声明一个ResolverUtil对象
            resolverUtil = new ResolverUtil<Class<?>>();
            for(Class<?> clz : typeHandlerMap.keySet()) {
	            //利用find函数找到这个包里所有的类，并且放到resolverUtil 的matches属性中
	            resolverUtil.find(new ResolverUtil.IsA(clz), enumPackage);
	            enumSet = resolverUtil.getClasses();
	            //通过for循环依次将加载
	            for (Class<?> type : enumSet) {
	            	log.info("enum="+clz.getName()+"; typeHandler: "+ type.toString());
	                // Ignore inner classes and interfaces (including package-info.java) and abstract classes
	            	//不处理内部类、接口、抽象类以及packgee_info类 
	            	if (!type.isAnonymousClass() && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
	            		//注册
	            		configuration.getTypeHandlerRegistry().register(type, null, typeHandlerMap.get(clz));
	                }
	            }
            }
        }
    }
}
