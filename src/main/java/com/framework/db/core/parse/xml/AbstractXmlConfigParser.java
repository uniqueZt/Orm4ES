package com.framework.db.core.parse.xml;

import com.framework.db.core.exception.ConfigException;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhangteng on 2018/8/17.
 */
public abstract class AbstractXmlConfigParser extends DefaultHandler{

    public void parseXmlFile(String configFilePath) throws SAXException,ParserConfigurationException,FileNotFoundException,IOException{
        if(StringUtils.isEmpty(configFilePath)){
            throw new ConfigException("xml文件路径不能为空");
        }
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        File configFile = ResourceUtils.getFile(configFilePath);
        saxParser.parse(configFile,this);
    }
}
