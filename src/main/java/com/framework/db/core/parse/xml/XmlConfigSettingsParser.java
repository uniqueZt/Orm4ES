package com.framework.db.core.parse.xml;

import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.model.cache.KeyValuePair;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.model.namespace.NamespaceSettings;
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
public class XmlConfigSettingsParser extends AbstractXmlConfigParser{

    private final static Logger LOGGER = LoggerFactory.getLogger(XmlConfigSettingsParser.class);

    private NamespaceSettings namespaceSettings = new NamespaceSettings();

    private final static String namespace = "namespace";

    private final static String mappingpath = "path";

    private final static String mappingname = "name";

    private List<KeyValuePair> mappingPathsAndNames = new LinkedList<KeyValuePair>();

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals(namespace)){
            parseNamespaceConfig(attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
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
            namespaceSettings.getNamespaceMap().put(mappingName,namespace);
        }catch (ClassNotFoundException e){
            LOGGER.error(e.getMessage());
            throw new ConfigException("namespace配置有误："+mappingName+"没有找到对应类");
        }
    }


}
