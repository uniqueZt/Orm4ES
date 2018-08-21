package com.framework.db.core.parse.xml;

import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.model.cache.KeyValuePair;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.model.namespace.NamespaceSettings;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class XmlConfigParser {

    private final static Logger LOGGER = LoggerFactory.getLogger(XmlConfigParser.class);

    private String settingPath;

    public void parse(){
        XmlConfigSettingsParser xmlConfigSettingsParser = new XmlConfigSettingsParser();
        try{
           xmlConfigSettingsParser.parseXmlFile(settingPath);
           List<KeyValuePair> mappingConfigs = xmlConfigSettingsParser.getMappingPathsAndNames();
           for(KeyValuePair keyValuePair:mappingConfigs){
               Namespace namespace = NamespaceSettings.getInstance().getNamespaceMap().get(keyValuePair.getKey());
               XmlConfigMappingParser xmlConfigMappingParser = new XmlConfigMappingParser(keyValuePair.getValue().toString(),namespace);
               xmlConfigMappingParser.parseXmlFile();
           }
        }catch (Exception e){
            LOGGER.error("解析失败",e);
            throw new ConfigException("解析失败",e);
        }
    }

    public String getSettingPath() {
        return settingPath;
    }

    public void setSettingPath(String settingPath) {
        this.settingPath = settingPath;
    }
}
