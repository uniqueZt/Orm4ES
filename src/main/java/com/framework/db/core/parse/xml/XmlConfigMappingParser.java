package com.framework.db.core.parse.xml;

import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.JsonTypeMapper;
import com.framework.db.core.model.mapper.MapTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.model.operate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.List;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class XmlConfigMappingParser extends AbstractXmlConfigParser{

    private final static Logger LOGGER = LoggerFactory.getLogger(XmlConfigMappingParser.class);

    private final static String INSERT = "insert";

    private final static String UPDATE_BY_KEY = "update-by-key";

    private final static String DELETE_BY_KEY = "delete-by-key";

    private final static String DELETE_BY_QUERY = "delete-by-query";

    private final static String SELECT = "select";

    private final static String SQL_SELECT = "sql-select";

    private final static String MAPPER = "mapper";

    private String configPath;

    private Namespace namespace;

    private CommonTypeMapper tempCommonTypeMapper;

    private SqlSelectTypeOperate tempSqlSelectTypeOperate;

    private StringBuilder formatSqlBuilder;

    public XmlConfigMappingParser(String configPath, Namespace namespace) {
        this.configPath = configPath;
        this.namespace = namespace;
        //初始化内置mapper
        namespace.getMapperMap().put(MapTypeMapper.MAP,new MapTypeMapper());
        namespace.getMapperMap().put(JsonTypeMapper.JSON,new JsonTypeMapper());
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        LOGGER.debug("开始解析："+configPath);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LOGGER.debug(configPath+" 解析结束");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals(MAPPER)){
            parseCommonTypeMapper(attributes);
        }else if(qName.equals(Mapper.ATTRIBUTE)){
            parseAttributesOfMapper(attributes);
        }else if(qName.equals(INSERT)){
            parseInsertTypeOperate(attributes);
        }else if(qName.equals(UPDATE_BY_KEY)){
            parseUpdateByKeyTypeOperate(attributes);
        }else if(qName.equals(DELETE_BY_KEY)){
            parseDeleteByKeyTypeOperate(attributes);
        }else if(qName.equals(DELETE_BY_QUERY)){
            parseDeleteByQueryTypeOperate(attributes);
        }else if(qName.equals(SELECT)){
            parseSelectTypeOperate(attributes);
        }else if(qName.equals(SQL_SELECT)){
           parseSqlSelectTypeOperate(attributes);
        }
    }

    private void parseSqlSelectTypeOperate(Attributes attributes){
       String id = attributes.getValue(Operate.ID);
       String result = attributes.getValue(Operate.RESULT);
       if(StringUtils.isEmpty(id) || StringUtils.isEmpty(result)){
           throw new ConfigException("sql-select类操作id和result必须配置");
       }
       SqlSelectTypeOperate sqlSelectTypeOperate = new SqlSelectTypeOperate();
       this.tempSqlSelectTypeOperate = sqlSelectTypeOperate;
       this.tempSqlSelectTypeOperate.setResultName(result);
       namespace.getOperateMap().put(id,sqlSelectTypeOperate);
    }

    private void parseSelectTypeOperate(Attributes attributes){
        String id = attributes.getValue(Operate.ID);
        String index = attributes.getValue(Operate.INDEX);
        String type = attributes.getValue(Operate.TYPE);
        String result = attributes.getValue(Operate.RESULT);
        String sizeStr = attributes.getValue(Operate.SIZE);
        String scroll = attributes.getValue(Operate.SCROLL);
        String timeStr = attributes.getValue(Operate.TIME);
        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(index) || StringUtils.isEmpty(result)){
            throw new ConfigException("select类操作id，index，type 属性必须配置");
        }
        if(StringUtils.isEmpty(type) &&(result.equals(MapTypeMapper.MAP) || result.equals(JsonTypeMapper.JSON))){
            throw new ConfigException("select类操作如果type为空，result必须是内置类型map或json");
        }
        if(!StringUtils.isEmpty(scroll) && (StringUtils.isEmpty(sizeStr) || StringUtils.isEmpty(timeStr))){
            throw new ConfigException("select类操作如果配置scroll查询，size和time均不能为空");
        }
        SelectTypeOperate selectTypeOperate = new SelectTypeOperate();
        selectTypeOperate.setIndex(index);
        selectTypeOperate.setType(type);
        selectTypeOperate.setResultName(result);
        selectTypeOperate.setSize(sizeStr == null?SelectTypeOperate.defaultSelectSize:Long.valueOf(sizeStr));
        selectTypeOperate.setScrollTime(timeStr == null?SelectTypeOperate.defaultScrollTIme:Long.valueOf(timeStr));
        if(!StringUtils.isEmpty(scroll)){
           selectTypeOperate.setScroll(true);
        }
        namespace.getOperateMap().put(id,selectTypeOperate);
    }

    private void parseDeleteByQueryTypeOperate(Attributes attributes){
        String id = attributes.getValue(Operate.ID);
        String index = attributes.getValue(Operate.INDEX);
        String type = attributes.getValue(Operate.TYPE);
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)|| StringUtils.isEmpty(id)){
            throw new ConfigException("delete-by-key类操作id,index,type属性必须配置");
        }
        DeleteByQueryTypeOperate deleteByQueryTypeOperate = new DeleteByQueryTypeOperate();
        deleteByQueryTypeOperate.setIndex(index);
        deleteByQueryTypeOperate.setType(type);
        namespace.getOperateMap().put(id,deleteByQueryTypeOperate);
    }

    private void parseDeleteByKeyTypeOperate(Attributes attributes){
        String id = attributes.getValue(Operate.ID);
        String index = attributes.getValue(Operate.INDEX);
        String type = attributes.getValue(Operate.TYPE);
        String refresh = attributes.getValue(Operate.REFRESH);
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)|| StringUtils.isEmpty(id)){
            throw new ConfigException("delete-by-key类操作id,index,type属性必须配置");
        }
        DeleteByKeyTypeOperate deleteByKeyTypeOperate = new DeleteByKeyTypeOperate();
        deleteByKeyTypeOperate.setIndex(index);
        deleteByKeyTypeOperate.setType(type);
        deleteByKeyTypeOperate.setRefresh(getRefreshType(refresh));
        namespace.getOperateMap().put(id,deleteByKeyTypeOperate);
    }

    private void parseUpdateByKeyTypeOperate(Attributes attributes){
        String id = attributes.getValue(Operate.ID);
        String index = attributes.getValue(Operate.INDEX);
        String type = attributes.getValue(Operate.TYPE);
        String paramter = attributes.getValue(Operate.PARAMETER);
        String refresh = attributes.getValue(Operate.REFRESH);
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)|| StringUtils.isEmpty(id) || StringUtils.isEmpty(paramter)){
            throw new ConfigException("update-by-key类操作id,index,type,paramter属性必须配置");
        }
        UpdateByKeyTypeOperate updateByKeyTypeOperate = new UpdateByKeyTypeOperate();
        updateByKeyTypeOperate.setIndex(index);
        updateByKeyTypeOperate.setType(type);
        updateByKeyTypeOperate.setParameterName(paramter);
        updateByKeyTypeOperate.setRefresh(getRefreshType(refresh));
        namespace.getOperateMap().put(id,updateByKeyTypeOperate);
    }

    private void parseInsertTypeOperate(Attributes attributes){
        String id = attributes.getValue(Operate.ID);
        String index = attributes.getValue(Operate.INDEX);
        String type = attributes.getValue(Operate.TYPE);
        String paramter = attributes.getValue(Operate.PARAMETER);
        String refresh = attributes.getValue(Operate.REFRESH);
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)|| StringUtils.isEmpty(id) || StringUtils.isEmpty(paramter)){
            throw new ConfigException("insert类操作id,index,type,paramter属性必须配置");
        }
        InsertTypeOperate insertTypeOperate = new InsertTypeOperate();
        insertTypeOperate.setIndex(index);
        insertTypeOperate.setType(type);
        insertTypeOperate.setParameterName(paramter);
        insertTypeOperate.setRefresh(getRefreshType(refresh));
        namespace.getOperateMap().put(id,insertTypeOperate);
    }

    private RefreshType getRefreshType(String refresh){
        if(StringUtils.isEmpty(refresh)){
            return RefreshType.NONE;
        }else{
            if("immediate".equals(refresh)){
                return RefreshType.IMMEDIATE;
            }else if("wait-until".equals(refresh)){
                return RefreshType.WAITUTIL;
            }else{
                return RefreshType.NONE;
            }
        }
    }

    private void parseAttributesOfMapper(Attributes attributes){
        com.framework.db.core.model.mapper.Attributes attributesOfMapper = new com.framework.db.core.model.mapper.Attributes();
        String property = attributes.getValue(com.framework.db.core.model.mapper.Attributes.PROPERTY);
        String column = attributes.getValue(com.framework.db.core.model.mapper.Attributes.COLUMN);
        String json = attributes.getValue(com.framework.db.core.model.mapper.Attributes.JSON);
        String nested = attributes.getValue(com.framework.db.core.model.mapper.Attributes.NESTED);
        if(!StringUtils.isEmpty(nested)){
            attributesOfMapper.setNested(true);
        }
        if(StringUtils.isEmpty(property) || StringUtils.isEmpty(column)){
            throw new ConfigException("配置attribute，property和column均不能为空");
        }
        attributesOfMapper.setColumn(column);
        attributesOfMapper.setProperty(property);
        if(!StringUtils.isEmpty(json) && com.framework.db.core.model.mapper.Attributes.JSON.equals(json)){
            attributesOfMapper.setJson(true);
        }
        tempCommonTypeMapper.addAttribute(attributesOfMapper);
    }

    private void parseCommonTypeMapper(Attributes attributes){
        tempCommonTypeMapper = new CommonTypeMapper();
        String className = attributes.getValue(Mapper.CLASS);
        String mapperName = attributes.getValue(Mapper.NAME);
        if(StringUtils.isEmpty(className) || StringUtils.isEmpty(mapperName)){
            throw new ConfigException("配置mapper，class或name均不能为空");
        }
        try{
              Class mapperClass = Class.forName(className);
              tempCommonTypeMapper.setMapperClass(mapperClass);
              namespace.getMapperMap().put(mapperName,tempCommonTypeMapper);
        }catch (ClassNotFoundException e){
              throw new ConfigException(mapperName+"对应的class找不到",e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(MAPPER.equals(qName)){
            if(tempCommonTypeMapper.getAttributes().size() <= 0){
                throw new ConfigException("mapper中的attribute不能为0");
            }else{
                List<com.framework.db.core.model.mapper.Attributes>  attributes = tempCommonTypeMapper.getAttributes();
                for(com.framework.db.core.model.mapper.Attributes attribute:attributes){
                    if(attribute.isNested()){
                        tempCommonTypeMapper.setHaveNestedProperty(true);
                        break;
                    }
                }
            }
            tempCommonTypeMapper = null;
        }else if(SQL_SELECT.equals(qName)){
            if(formatSqlBuilder.length() == 0){
                throw new ConfigException("sql-select类操作sql不能配置为空");
            }
            String originSql = formatSqlBuilder.toString();
            tempSqlSelectTypeOperate.setFormatSql(originSql);
            SqlSelectTypeOperate.SqlWithParamterBuilder sqlWithParamterBuilder = parseParamSql(originSql);
            tempSqlSelectTypeOperate.setSqlWithParamterBuilder(sqlWithParamterBuilder);
            formatSqlBuilder = null;
            tempSqlSelectTypeOperate = null;
        }
    }

    private SqlSelectTypeOperate.SqlWithParamterBuilder parseParamSql(String originSql){
        originSql = originSql.replace("!=","<>");
        SqlSelectTypeOperate.SqlWithParamterBuilder sqlWithParamterBuilder = new SqlSelectTypeOperate.SqlWithParamterBuilder();
        int startIndex = -1;
        int endIndex = -1;
        char[] sqlCharArray = originSql.toCharArray();
        StringBuilder tempBuilder = new StringBuilder();
        boolean flag = false;
        for(int i=0;i<sqlCharArray.length;i++){
            if(sqlCharArray[i] == SqlSelectTypeOperate.SqlWithParamterBuilder.OPEN){
                sqlWithParamterBuilder.appendSqlSegment(tempBuilder.toString());
                tempBuilder = new StringBuilder();
                startIndex = i;
                flag = true;
            }else if(sqlCharArray[i] == SqlSelectTypeOperate.SqlWithParamterBuilder.CLOSE){
                endIndex = i;
                String paramName = new String(sqlCharArray,startIndex+1,endIndex - startIndex -1);
                sqlWithParamterBuilder.appendParameter(paramName);
                flag = false;
            }else{
                if(!flag){
                    tempBuilder.append(sqlCharArray[i]);
                }
            }
        }
        sqlWithParamterBuilder.appendSqlSegment(tempBuilder.toString());
        return sqlWithParamterBuilder;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(tempSqlSelectTypeOperate != null && length >0){
            if(formatSqlBuilder == null){
               formatSqlBuilder = new StringBuilder();
            }
            formatSqlBuilder.append(ch, start, length);
        }
    }

    public void parseXmlFile() throws Exception{
        parseXmlFile(this.configPath);
    }

    public Namespace getNamespace() {
        return namespace;
    }
}
