package com.framework.db.core.automation;

import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.model.cache.KeyValuePair;
import com.framework.db.core.model.namespace.GlobalSettings;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.parse.GlobalSettingsParser;
import com.framework.db.core.parse.annotation.config.AnnotationsConfigParser;
import com.framework.db.core.parse.xml.XmlConfigMappingParser;
import com.framework.db.core.proxy.MapperInterfaceProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangteng on 2018/9/27.
 */
public  class MapperInterfaceRegister implements BeanDefinitionRegistryPostProcessor,ApplicationContextAware{

    protected  ApplicationContext applicationContext;

    private String settingConfigPath;

    private String basePackage;

    List<KeyValuePair> xmlMappingConfigs;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try{
            parseGlobalConfig();
            parseXmlConfig();
            parseAnnotationConfig(beanDefinitionRegistry);
        }catch (Exception e){
            throw new ConfigException("配置出错",e);
        }
        registerMapperInterface(beanDefinitionRegistry);
    }

    private void registerMapperInterface(BeanDefinitionRegistry beanDefinitionRegistry){
        GlobalSettings globalSettings = GlobalSettings.getInstance();
        for(Map.Entry<String, com.framework.db.core.model.namespace.Namespace> entry:globalSettings.getNamespaceMap().entrySet()){
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
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    private void parseAnnotationConfig(BeanDefinitionRegistry beanDefinitionRegistry){
        if(!StringUtils.isEmpty(basePackage)){
            AnnotationsConfigParser annotationsConfigParser = new AnnotationsConfigParser(beanDefinitionRegistry);
            annotationsConfigParser.setResourceLoader(this.applicationContext);
            //annotationsConfigParser.setScopeMetadataResolver(ScopeMetadataResolver);
            annotationsConfigParser.scan(basePackage);
        }
    }

    private void parseXmlConfig() throws Exception{
        if(null != xmlMappingConfigs && xmlMappingConfigs.size() > 0){
            for(KeyValuePair keyValuePair:xmlMappingConfigs){
                Namespace namespace = GlobalSettings.getInstance().getNamespaceMap().get(keyValuePair.getKey());
                XmlConfigMappingParser xmlConfigMappingParser = new XmlConfigMappingParser(keyValuePair.getValue().toString(),namespace);
                xmlConfigMappingParser.parseXmlFile();
            }
        }
    }


    private void parseGlobalConfig() throws SAXException,ParserConfigurationException,IOException{
        GlobalSettingsParser globalSettingsParser = new GlobalSettingsParser();
        globalSettingsParser.parseXmlFile(settingConfigPath);
        xmlMappingConfigs = globalSettingsParser.getMappingPathsAndNames();
    }

    public String getSettingConfigPath() {
        return settingConfigPath;
    }

    public void setSettingConfigPath(String settingConfigPath) {
        this.settingConfigPath = settingConfigPath;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }
}
