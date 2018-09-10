package com.framework.db.core.parse.annotation.config;

import com.framework.db.core.automation.MapperInterfaceFactoryBean;
import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.model.mapper.Attributes;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.JsonTypeMapper;
import com.framework.db.core.model.mapper.MapTypeMapper;
import com.framework.db.core.model.namespace.NamespaceSettings;
import com.framework.db.core.model.operate.*;
import com.framework.db.core.parse.annotation.config.mapper.Attribute;
import com.framework.db.core.parse.annotation.config.mapper.Mapper;
import com.framework.db.core.parse.annotation.config.operate.*;
import com.framework.db.core.proxy.MapperInterfaceProxy;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by zhangteng on 2018/8/31.
 */
public final class AnnotationsConfigParser extends ClassPathBeanDefinitionScanner{

    private Map<Class,Map<String, com.framework.db.core.model.mapper.Mapper>> mapperCache = new HashMap<>();

    private Map<Class, com.framework.db.core.model.namespace.Namespace> namespaceCache = new HashMap<>();

    public AnnotationsConfigParser(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected void registerDefaultFilters() {
        //super.registerDefaultFilters();
        super.addIncludeFilter(new AnnotationTypeFilter(Mapper.class));
        super.addIncludeFilter(new AnnotationTypeFilter(Namespace.class));
    }



    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders =  super.doScan(basePackages);
        for(BeanDefinitionHolder beanDefinitionHolder:beanDefinitionHolders){
            GenericBeanDefinition beanDefinition = (GenericBeanDefinition)beanDefinitionHolder.getBeanDefinition();
            try{
                Class clazz = Class.forName(beanDefinition.getBeanClassName());
                Annotation[] annotations = clazz.getDeclaredAnnotations();
                for(Annotation annotation:annotations){
                    if(annotation instanceof Mapper){
                        parseMapper((Mapper)annotation,clazz);
                        break;
                    }
                    if(annotation instanceof Namespace){
                        parseNamespace(clazz);
                        break;
                    }

                }
            }catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        //组装namespace
        assembleNamespaceSetting();
        NamespaceSettings namespaceSettings = NamespaceSettings.getInstance();
        for(Map.Entry<String, com.framework.db.core.model.namespace.Namespace> entry:namespaceSettings.getNamespaceMap().entrySet()){
            BeanDefinition beanDefinition = getBeanDefinition(entry);
            super.getRegistry().registerBeanDefinition(entry.getKey(),beanDefinition);
        }
        return null;
    }

    @Override
    protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
        //super.registerBeanDefinition(definitionHolder, registry);
    }

    private BeanDefinition getBeanDefinition(Map.Entry<String, com.framework.db.core.model.namespace.Namespace> entry){
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MapperInterfaceFactoryBean.class);
        beanDefinitionBuilder.addPropertyValue("mapperInterfaceProxy",new MapperInterfaceProxy<>().setNamespace(entry.getValue()));
        return beanDefinitionBuilder.getBeanDefinition();
    }

    private void assembleNamespaceSetting(){
        NamespaceSettings namespaceSettings = NamespaceSettings.getInstance();
        for(Map.Entry<Class, com.framework.db.core.model.namespace.Namespace> entry:namespaceCache.entrySet()){
            Map<String, com.framework.db.core.model.mapper.Mapper> mapperMap = mapperCache.get(entry.getKey());
            if(null != mapperMap){
                com.framework.db.core.model.namespace.Namespace namespace = entry.getValue();
                for(Map.Entry<String, com.framework.db.core.model.mapper.Mapper> mapperEntry:mapperMap.entrySet()){
                    namespace.getMapperMap().put(mapperEntry.getKey(),mapperEntry.getValue());
                }
            }
            namespaceSettings.getNamespaceMap().put(entry.getKey().getName(),entry.getValue());
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        boolean superresult = super.isCandidateComponent(beanDefinition);
        return superresult || beanDefinition.getMetadata().isInterface();
    }

    private void parseMapper(Mapper mapperAnnotation, Class mapperClass){
        CommonTypeMapper commonTypeMapper = new CommonTypeMapper();
        commonTypeMapper.setMapperClass(mapperClass);
        //解析attribute
        for(Field field:mapperClass.getDeclaredFields()){
           Annotation[] fieldAnnotations = field.getDeclaredAnnotations();
           for(Annotation annotation: fieldAnnotations){
               if(annotation instanceof Attribute){
                   Attributes attribute = new Attributes();
                   attribute.setProperty(field.getName());
                   attribute.setColumn(((Attribute) annotation).column());
                   attribute.setJson(((Attribute) annotation).isJson());
                   attribute.setNested(((Attribute) annotation).isNested());
                   if(attribute.isNested()){
                       commonTypeMapper.setHaveNestedProperty(true);
                   }
                   commonTypeMapper.addAttribute(attribute);
                   break;
               }
           }
        }
        if(commonTypeMapper.getAttributes().size() == 0){
            throw new ConfigException("mapper内映射数量不能为空");
        }
        Map<String, com.framework.db.core.model.mapper.Mapper> nameSpaceMapperCache =  mapperCache.get(mapperAnnotation.namespace());
        if(nameSpaceMapperCache == null){
            nameSpaceMapperCache = new HashMap<>();
            mapperCache.put(mapperAnnotation.namespace(),nameSpaceMapperCache);
        }
        com.framework.db.core.model.mapper.Mapper mapper = nameSpaceMapperCache.get(mapperAnnotation.name());
        if(mapper == null){
            nameSpaceMapperCache.put(mapperAnnotation.name(),commonTypeMapper);
        }else{
            throw new ConfigException("Mapper映射定义重复");
        }
    }

    public void parseNamespace(Class namespaceInterface){
        com.framework.db.core.model.namespace.Namespace namespace = new com.framework.db.core.model.namespace.Namespace();
        namespace.setNamespaceClass(namespaceInterface);
        Method[] methods = namespaceInterface.getDeclaredMethods();
        for(Method method :methods){
            Operate operate = null;
            for(Annotation annotation:method.getDeclaredAnnotations()){
                if(annotation instanceof Insert){
                    operate = parseInsertTypeOperate((Insert) annotation);
                    break;
                }
                if(annotation instanceof BatchInsert){
                    operate = parseBatchInsertTypeOperate((BatchInsert)annotation);
                    break;
                }
                if(annotation instanceof UpdateByKey){
                    operate = parseUpdateByKeyTypeOperate((UpdateByKey)annotation);
                    break;
                }
                if(annotation instanceof DeleteByKey){
                    operate = parseDeleteByKeyTypeOperate((DeleteByKey)annotation);
                    break;
                }
                if(annotation instanceof DeleteByQuery){
                    operate = parseDeleteByQueryOperate((DeleteByQuery)annotation);
                    break;
                }
                if(annotation instanceof Select){
                    operate = parseSelectTypeOperate((Select)annotation);
                    break;
                }
                if(annotation instanceof SqlSelect){
                    operate = parseSqlSelectTypeOperate((SqlSelect)annotation);
                    break;
                }
            }
            if(null != operate){
                namespace.getOperateMap().put(method.getName(),operate);
            }
        }
        namespace.getMapperMap().put(MapTypeMapper.MAP,new MapTypeMapper());
        namespace.getMapperMap().put(JsonTypeMapper.JSON,new JsonTypeMapper());
        namespaceCache.put(namespaceInterface,namespace);
    }

    private InsertTypeOperate parseInsertTypeOperate(Insert insertAnnotation){
        String index = insertAnnotation.index();
        String type = insertAnnotation.type();
        String parameter = insertAnnotation.parameter();
        RefreshType refreshType = insertAnnotation.refresh();
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type) || StringUtils.isEmpty(parameter)){
            throw new ConfigException("insert类操作id,index,type,paramter属性必须配置");
        }
        InsertTypeOperate insertTypeOperate = new InsertTypeOperate();
        insertTypeOperate.setIndex(index);
        insertTypeOperate.setType(type);
        insertTypeOperate.setRefresh(refreshType);
        insertTypeOperate.setParameterName(parameter);
        return insertTypeOperate;
    }

    private UpdateByKeyTypeOperate parseUpdateByKeyTypeOperate(UpdateByKey updateByKeyAnnotation){
        String index = updateByKeyAnnotation.index();
        String type = updateByKeyAnnotation.type();
        String parameter = updateByKeyAnnotation.parameter();
        RefreshType refreshType = updateByKeyAnnotation.refresh();
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)|| StringUtils.isEmpty(parameter)){
            throw new ConfigException("update-by-key类操作id,index,type,paramter属性必须配置");
        }
        UpdateByKeyTypeOperate updateByKeyTypeOperate = new UpdateByKeyTypeOperate();
        updateByKeyTypeOperate.setIndex(index);
        updateByKeyTypeOperate.setType(type);
        updateByKeyTypeOperate.setParameterName(parameter);
        updateByKeyTypeOperate.setRefresh(refreshType);
        return updateByKeyTypeOperate;
    }

    private DeleteByKeyTypeOperate parseDeleteByKeyTypeOperate(DeleteByKey deleteByKeyAnnotation){
        String index = deleteByKeyAnnotation.index();
        String type = deleteByKeyAnnotation.type();
        RefreshType refreshType = deleteByKeyAnnotation.refresh();
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)){
            throw new ConfigException("delete-by-key类操作id,index,type属性必须配置");
        }
        DeleteByKeyTypeOperate deleteByKeyTypeOperate = new DeleteByKeyTypeOperate();
        deleteByKeyTypeOperate.setIndex(index);
        deleteByKeyTypeOperate.setType(type);
        deleteByKeyTypeOperate.setRefresh(refreshType);
        return deleteByKeyTypeOperate;
    }

    private DeleteByQueryTypeOperate parseDeleteByQueryOperate(DeleteByQuery deleteByQueryAnnotation){
        String index = deleteByQueryAnnotation.index();
        String type = deleteByQueryAnnotation.type();
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)){
            throw new ConfigException("delete-by-key类操作id,index,type属性必须配置");
        }
        DeleteByQueryTypeOperate deleteByQueryTypeOperate = new DeleteByQueryTypeOperate();
        deleteByQueryTypeOperate.setIndex(index);
        deleteByQueryTypeOperate.setType(type);
        return deleteByQueryTypeOperate;
    }

    private SelectTypeOperate parseSelectTypeOperate(Select selectAnnotation){
        String index = selectAnnotation.index();
        String type = selectAnnotation.type();
        String result = selectAnnotation.result();
        long size = selectAnnotation.size();
        long time = selectAnnotation.time();
        boolean scroll = selectAnnotation.scroll();
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(result)){
            throw new ConfigException("select类操作id，index，type 属性必须配置");
        }
        if(StringUtils.isEmpty(type) &&(result.equals(MapTypeMapper.MAP) || result.equals(JsonTypeMapper.JSON))){
            throw new ConfigException("select类操作如果type为空，result必须是内置类型map或json");
        }
        if(scroll && (size == 0L || time == 0L)){
            throw new ConfigException("select类操作如果配置scroll查询，size和time均需要配置");
        }
        SelectTypeOperate selectTypeOperate = new SelectTypeOperate();
        selectTypeOperate.setIndex(index);
        selectTypeOperate.setType(type);
        selectTypeOperate.setResultName(result);
        selectTypeOperate.setSize(size == 0L?SelectTypeOperate.defaultSelectSize:size);
        selectTypeOperate.setScrollTime(time == 0L?SelectTypeOperate.defaultScrollTIme:time);
        selectTypeOperate.setScroll(scroll);
        return selectTypeOperate;
    }

    private SqlSelectTypeOperate parseSqlSelectTypeOperate(SqlSelect sqlSelectAnnotation){
        String result = sqlSelectAnnotation.result();
        String originSql = sqlSelectAnnotation.sql();
        if(StringUtils.isEmpty(originSql) || StringUtils.isEmpty(result)){
            throw new ConfigException("sql-select类操作sql和result必须配置");
        }
        SqlSelectTypeOperate sqlSelectTypeOperate = new SqlSelectTypeOperate();
        sqlSelectTypeOperate.setResultName(result);
        sqlSelectTypeOperate.setFormatSql(originSql);
        SqlSelectTypeOperate.SqlWithParamterBuilder sqlWithParamterBuilder = parseParamSql(originSql);
        sqlSelectTypeOperate.setSqlWithParamterBuilder(sqlWithParamterBuilder);
        return sqlSelectTypeOperate;
    }

    private BatchInsertTypeOperate parseBatchInsertTypeOperate(BatchInsert batchInsertAnnotation){
        String index = batchInsertAnnotation.index();
        String type = batchInsertAnnotation.type();
        String parameter = batchInsertAnnotation.parameter();
        RefreshType refreshType = batchInsertAnnotation.refresh();
        if(StringUtils.isEmpty(index) || StringUtils.isEmpty(type)|| StringUtils.isEmpty(parameter)){
            throw new ConfigException("insert类操作index,type,paramter属性必须配置");
        }
        BatchInsertTypeOperate batchInsertTypeOperate = new BatchInsertTypeOperate();
        batchInsertTypeOperate.setIndex(index);
        batchInsertTypeOperate.setType(type);
        batchInsertTypeOperate.setParameterName(parameter);
        batchInsertTypeOperate.setRefresh(refreshType);
        return batchInsertTypeOperate;
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
}
