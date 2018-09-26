package com.framework.db.core.parse;

import com.framework.db.core.cache.CacheLevel;
import com.framework.db.core.cache.EvictStrategy;
import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.model.cache.CacheConfig;
import com.framework.db.core.model.cache.KeyValuePair;
import com.framework.db.core.model.namespace.GlobalSettings;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.parse.xml.AbstractXmlConfigParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class GlobalSettingsParser extends AbstractXmlConfigParser{

    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalSettingsParser.class);

    private final static String namespace = "namespace";

    private final static String cacheconfig = "cache-config";

    private final static String level = "level";

    private final static String evict = "evict";

    private final static String maxsize = "maxsize";

    private final static String expiretime = "expiretime";

    private final static String expiretimeafterwrite = "keyExpiretime";

    private final static String mappingpath = "path";

    private final static String mappingname = "name";

    private List<KeyValuePair> mappingPathsAndNames = new LinkedList<KeyValuePair>();

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        LOGGER.debug("start parse setting file");
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LOGGER.debug("setting file parse end");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals(namespace)){
            parseNamespaceConfig(attributes);
        }else if(qName.equals(cacheconfig)){
            parseCacheConfig(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    private void parseCacheConfig(Attributes attributes){
        CacheConfig cacheConfig = new CacheConfig();
        String evictStr = attributes.getValue(evict);
        String maxsizeStr = attributes.getValue(maxsize);
        String expireTimeStr = attributes.getValue(expiretime);
        String keyExpireTimeStr = attributes.getValue(expiretimeafterwrite);
        String levelStr = attributes.getValue(level);
        EvictStrategy evictStrategy = EvictStrategy.FIFO;
        if(null != evictStr){
            switch (evictStr){
                case "fifo":
                    evictStrategy = EvictStrategy.FIFO;
                    break;
                case "lru":
                    evictStrategy = EvictStrategy.LRU;
                    break;
                default:
                    break;
            }
        }
        int maxSize = Integer.valueOf(maxsizeStr);
        long expireTime = Long.valueOf(expireTimeStr);
        long expireTimeAfterWrite = Long.valueOf(expiretimeafterwrite);
        CacheLevel cacheLevel = CacheLevel.STRONG;
        switch (levelStr){
            case "strong":
                cacheLevel = CacheLevel.STRONG;
                break;
            case "soft":
                cacheLevel = CacheLevel.SOFT;
                break;
            case "weak":
                cacheLevel = CacheLevel.WEAK;
                break;
            default:
                break;
        }
        cacheConfig.setCacheLevel(cacheLevel);
        cacheConfig.setEvictStrategy(evictStrategy);
        cacheConfig.setMaxSize(maxSize);
        cacheConfig.setExpireTime(expireTime);
        cacheConfig.setExpireTimeAfterWrite(expireTimeAfterWrite);
        GlobalSettings.getInstance().setCacheConfig(cacheConfig);
    }

    private void parseNamespaceConfig(Attributes attributes){
        String mappingPath = attributes.getValue(mappingpath);
        String mappingName = attributes.getValue(mappingname);
        if(StringUtils.isEmpty(mappingPath) || StringUtils.isEmpty(mappingName)){
            throw new ConfigException("namespace配置有误：path或name配置为空");
        }
        mappingPathsAndNames.add(new KeyValuePair(mappingName,mappingPath));
        try{
            Class clazz = Class.forName(mappingName);
            Namespace namespace = new Namespace();
            namespace.setNamespaceClass(clazz);
            GlobalSettings.getInstance().getNamespaceMap().put(mappingName,namespace);
        }catch (ClassNotFoundException e){
            LOGGER.error(e.getMessage());
            throw new ConfigException("namespace配置有误："+mappingName+"没有找到对应类");
        }
    }

    public List<KeyValuePair> getMappingPathsAndNames() {
        return mappingPathsAndNames;
    }

    public void setMappingPathsAndNames(List<KeyValuePair> mappingPathsAndNames) {
        this.mappingPathsAndNames = mappingPathsAndNames;
    }
}
