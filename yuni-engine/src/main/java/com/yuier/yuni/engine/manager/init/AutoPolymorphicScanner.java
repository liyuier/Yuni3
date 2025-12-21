package com.yuier.yuni.engine.manager.init;

import com.yuier.yuni.core.anno.PolymorphicSubType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Title: AutoPolymorphicScanner
 * @Author yuier
 * @Package com.yuier.yuni.engine.manager.init
 * @Date 2025/12/22 5:38
 * @description: Polymorphic 注解扫描
 */

@Component
public class AutoPolymorphicScanner
//        implements BeanFactoryPostProcessor
{

    @Autowired  // 🔥 使用 @Autowired 注解注入依赖
    private PolymorphicRegistrationProcessor registrationProcessor;

//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        try {
//            scanAndRegisterPolymorphicTypes();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to auto-register polymorphic types", e);
//        }
//    }

    public void scanAndRegisterPolymorphicTypes() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);

        // 扫描当前包下的所有类
        Resource[] resources = resolver.getResources("classpath*:com/yuier/yuni/core/model/**/*.class");

        for (Resource resource : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();

            // 检查是否标注了 @PolymorphicSubType
            if (annotationMetadata.hasAnnotation(PolymorphicSubType.class.getName())) {
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> clazz = Class.forName(className);

                // 获取注解属性
                Map<String, Object> annotationAttributes =
                        annotationMetadata.getAnnotationAttributes(PolymorphicSubType.class.getName());
                String customTypeName = (String) annotationAttributes.get("value");

                // 如果用户没有指定 type 名称，则自动推断
                String typeName = customTypeName;
                if (typeName == null || typeName.trim().isEmpty()) {
                    typeName = inferTypeName(clazz);
                }

//                registrationProcessor.registerSubType(clazz, typeName);
            }
        }
    }

    private String inferTypeName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        if (simpleName.endsWith("Event")) {
            return lowerFirst(simpleName.substring(0, simpleName.length() - 5));
        } else if (simpleName.endsWith("Segment")) {
            return lowerFirst(simpleName.substring(0, simpleName.length() - 7));
        }
        return lowerFirst(simpleName);
    }

    private String lowerFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
