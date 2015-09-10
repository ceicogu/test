package com.qihao.toy.dal.domain.typehandlers;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.lang.reflect.Modifier;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.qihao.shared.base.DescedEnum;
import com.qihao.shared.base.IntEnum;

/**
 * 注册typeHandler用
 * DescedEnum & IntEnum
 */
@Slf4j
public class TypeHandlerRegister {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    @Getter
    @Setter
    private String            scanPackage;

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
            resolverUtil = new ResolverUtil<Class<?>>();
            //注册DescedEnum
            resolverUtil.find(new ResolverUtil.IsA(DescedEnum.class), enumPackage);
            enumSet = resolverUtil.getClasses();
            for (Class<?> type : enumSet) {
            	log.info("typeHandler: "+ type.toString());
                // Ignore inner classes and interfaces (including package-info.java) and abstract classes
                if (!type.isAnonymousClass() && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                    configuration.getTypeHandlerRegistry().register(type, null, DescedEnumTypeHandler.class);
                }
            }
            //注册IntEnum
            resolverUtil.find(new ResolverUtil.IsA(IntEnum.class), enumPackage);
            enumSet = resolverUtil.getClasses();
            for (Class<?> type : enumSet) {
            	log.info("typeHandler: "+ type.toString());
                // Ignore inner classes and interfaces (including package-info.java) and abstract classes
                if (!type.isAnonymousClass() && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                    configuration.getTypeHandlerRegistry().register(type, null, IntEnumTypeHandler.class);
                }
            }
        }

    }
}
