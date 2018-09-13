package com.framework.db.core.parse.annotation.config;

import com.framework.db.core.automation.MapperInterfaceFactoryBean;
import com.framework.db.core.model.namespace.*;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.proxy.MapperInterfaceProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.Map;

/**
 * Created by zhangteng on 2018/8/31.
 */
public class AnnotationConfigLoader implements BeanDefinitionRegistryPostProcessor,ApplicationContextAware{

    private String basePackage;

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        AnnotationsConfigParser annotationsConfigParser = new AnnotationsConfigParser(beanDefinitionRegistry);
        annotationsConfigParser.setResourceLoader(this.applicationContext);
        //annotationsConfigParser.setScopeMetadataResolver(ScopeMetadataResolver);
        annotationsConfigParser.scan(basePackage);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }




    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
          this.applicationContext = applicationContext;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
