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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.context.annotation.ScopedProxyMode;

import java.util.Map;

/**
 * Created by zhangteng on 2018/8/31.
 */
public class AnnotationConfigLoader implements BeanFactoryPostProcessor,ApplicationContextAware{

    private String basePackage;

    private ApplicationContext applicationContext;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
          BeanDefinitionRegistry beanDefinitionRegistry =  (BeanDefinitionRegistry)configurableListableBeanFactory;
          AnnotationsConfigParser annotationsConfigParser = new AnnotationsConfigParser(beanDefinitionRegistry);
          annotationsConfigParser.setResourceLoader(this.applicationContext);
          //annotationsConfigParser.setScopeMetadataResolver(ScopeMetadataResolver);
          annotationsConfigParser.scan(basePackage);
          NamespaceSettings namespaceSettings = NamespaceSettings.getInstance();
          for(Map.Entry<String, com.framework.db.core.model.namespace.Namespace> entry:namespaceSettings.getNamespaceMap().entrySet()){
            BeanDefinition beanDefinition = getBeanDefinition(entry);
            beanDefinitionRegistry.registerBeanDefinition(entry.getKey(),beanDefinition);
          }
    }

    private BeanDefinition getBeanDefinition(Map.Entry<String,Namespace> entry){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperInterfaceFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("mapperInterfaceProxy",new MapperInterfaceProxy<>().setNamespace(entry.getValue()));
        return beanDefinitionBuilder.getBeanDefinition();
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
