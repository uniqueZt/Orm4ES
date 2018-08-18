package com.framework.db.core.automation;

import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.model.namespace.NamespaceSettings;
import com.framework.db.core.parse.xml.XmlConfigParser;
import com.framework.db.core.proxy.MapperInterfaceProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Map;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class MapperInterfaceDefinitionRegister implements BeanDefinitionRegistryPostProcessor{

    private String settingConfigPath;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        parseXmlConfig();
        NamespaceSettings namespaceSettings = NamespaceSettings.getInstance();
        for(Map.Entry<String,Namespace> entry:namespaceSettings.getNamespaceMap().entrySet()){
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

    private void parseXmlConfig(){
        XmlConfigParser xmlConfigParser = new XmlConfigParser();
        xmlConfigParser.setSettingPath(settingConfigPath);
        xmlConfigParser.parse();
    }

    public String getSettingConfigPath() {
        return settingConfigPath;
    }

    public void setSettingConfigPath(String settingConfigPath) {
        this.settingConfigPath = settingConfigPath;
    }
}
