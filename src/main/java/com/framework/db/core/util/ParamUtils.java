package com.framework.db.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.framework.db.core.exception.ConfigException;
import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.exception.ParamParseExcetion;
import com.framework.db.core.model.mapper.Attributes;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.param.*;
import com.framework.db.core.parse.annotation.parameter.Key;
import com.framework.db.core.parse.annotation.parameter.Query;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.nlpcn.es4sql.domain.*;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class ParamUtils {

    private static final Set<Class> leafSet = new HashSet<>();

    static {
        leafSet.add(String.class);
        leafSet.add(Integer.class);
        leafSet.add(Double.class);
        leafSet.add(Long.class);
        leafSet.add(Boolean.class);
        leafSet.add(Float.class);
        leafSet.add(Short.class);
        leafSet.add(Collection.class);
        leafSet.add(Map.class);
    }

    private static Class getListType(Field field) throws Exception{
        Type fieldType = field.getGenericType();
        if(ParameterizedType.class.isAssignableFrom(fieldType.getClass())){
            for(Type t:((ParameterizedType)fieldType).getActualTypeArguments()){
                return Class.forName(t.getTypeName());
            }
        }
        return null;
    }

    public static JSONObject parseOriginResultToJSON(Map<String,Object> originResult){
        JSONObject result = new JSONObject();
        for(Map.Entry<String,Object> entry:originResult.entrySet()){
            result.put(entry.getKey(),entry.getValue());
        }
        return result;
    }

    public static <T> T parseOrginResultToBean(Map<String,Object> originResult,Class<T> clazz,List<Attributes> attributes) throws Exception{
        T result = (T)clazz.newInstance();
        for(Attributes attribute:attributes){
            Object readValue = originResult.get(attribute.getColumn());
            if(null != readValue){
                String[] nestedProperties = attribute.getProperty().split("\\.");
                if(nestedProperties.length == 1){
                    Field field = findField(clazz,nestedProperties[0]);
                    if(null != field){
                        field.setAccessible(true);
                        if(attribute.isJson()){
                            if(List.class.isAssignableFrom(field.getType())){
                                readValue = JSON.parseArray((String)readValue,getListType(field));
                            }else{
                                readValue = JSON.parseObject((String)readValue,field.getType());
                            }
                        }
                        field.set(result,readValue);
                    }
                }else{
                    Object nestedOuterBean = result;
                    Class nestedOuterClass = clazz;
                    Object nestedInnerBean = null;
                    for(int i=0;i< nestedProperties.length;i++){
                        Field field = findField(nestedOuterClass,nestedProperties[i]);
                        field.setAccessible(true);
                        nestedInnerBean = field.get(nestedOuterBean);
                        if(nestedInnerBean == null){
                            if(i != nestedProperties.length -1){
                                nestedInnerBean = field.getType().newInstance();
                            }else{
                                if(attribute.isJson()){
                                    if(List.class.isAssignableFrom(field.getType())){
                                        readValue = JSON.parseArray((String)readValue,getListType(field));
                                    }else{
                                        readValue = JSON.parseObject((String)readValue,field.getType());
                                    }
                                }
                                nestedInnerBean = readValue;
                            }
                            field.set(nestedOuterBean,nestedInnerBean);
                        }
                        nestedOuterBean = nestedInnerBean;
                        nestedOuterClass = nestedInnerBean.getClass();
                    }
                }
            }
        }
        return result;
    }

    private static Field findField(Class clazz,String fieldName){
        Map<String,Field> fieldMap = getFieldMapOfBean(clazz);
        return fieldMap.get(fieldName);
    }

    public static boolean isLeaf(Class instanceClass){
        if(leafSet.contains(instanceClass)){
             return true;
         }
         for(Class clazz:leafSet) {
             if (clazz.isAssignableFrom(instanceClass)) {
                 return true;
             }
         }
         return false;
    }

    public static Map<String,Object> beanToFlatMap(List<Attributes> attributes,Object bean,boolean isAllowNull){
        Map<String,Object> result = new HashMap();
        Class clazz = bean.getClass();
        for(Attributes attribute:attributes){
            if(!attribute.isNested()){
                dealNonNestedProperty(clazz,attribute,bean,isAllowNull,result);
            }else{
                dealNestedProperty(clazz,attribute,bean,isAllowNull,result);
            }
        }
        return result;
    }

    public static Map<String,Object> beanToMap(List<Attributes> attributes,Object bean,boolean isAllowNull){
        Map<String,Object> result = new HashMap<>();
        Class clazz = bean.getClass();
        for(Attributes attribute:attributes){
            dealNonNestedProperty(clazz,attribute,bean,isAllowNull,result);
        }
        return result;
    }


    private static Object getFielidValue(Field field, Object bean, Class clazz) throws IllegalAccessException{
        field.setAccessible(true);
        Object param = field.get(bean);
        return param;
    }

    public static void dealNonNestedProperty(Class clazz,Attributes attribute,Object bean,boolean isAllowNull,Map<String,Object> result){
        try{
            Field field = clazz.getDeclaredField(attribute.getProperty());
            Object param = getFielidValue(field,bean,clazz);
            if(attribute.isJson()){
                String paramJsonStr = param == null ? null:JSON.toJSONString(param);
                if(isAllowNull){
                    result.put(attribute.getColumn(),paramJsonStr);
                }else{
                    if(paramJsonStr != null){
                        result.put(attribute.getColumn(),paramJsonStr);
                    }
                }
            }else{
                if(!isLeaf(field.getType())){
                    throw new ExecuteException("属性不是基本类型，请检查配置");
                }
                if(isAllowNull){
                    result.put(attribute.getColumn(),param);
                }else{
                    if(param != null){
                        result.put(attribute.getColumn(),param);
                    }
                }
            }
        }catch (NoSuchFieldException e){
            throw new ExecuteException("属性获取失败，请检查映射",e);
        }catch (IllegalAccessException e){
            throw new ExecuteException("属性不可访问",e);
        }
    }

    public static void dealNestedProperty(Class clazz,Attributes attribute,Object bean,boolean isAllowNull,Map<String,Object> result){
        try{
            Class currentClass = clazz;
            Object currentBean = bean;
            String[] properties = attribute.getProperty().split("\\.");
            if(properties.length <=1){
                throw new ExecuteException("property 配置不符合嵌套类型规范");
            }
            Object finalParam = null;
            for(int i = 0;i<properties.length;i++){
                Field field = currentClass.getDeclaredField(properties[i]);
                Object param = getFielidValue(field,currentBean,currentClass);
                if(i == properties.length - 1){
                    finalParam = param;
                    if(null != param){
                       currentClass = param.getClass();
                    }
                }else{
                    if(param != null){
                        currentBean = param;
                        currentClass = param.getClass();
                    }else{
                        finalParam = null;
                    }
                }
            }
            if(attribute.isJson()){
                String paramJsonStr = finalParam == null ? null:JSON.toJSONString(finalParam);
                if(isAllowNull){
                    result.put(attribute.getColumn(),paramJsonStr);
                }else{
                    if(paramJsonStr != null){
                        result.put(attribute.getColumn(),paramJsonStr);
                    }
                }
            }else{
                if(finalParam != null){
                    if(!isLeaf(currentClass)){
                        throw new ExecuteException("属性不是基本类型，请检查配置");
                    }
                    result.put(attribute.getColumn(),finalParam);
                }else{
                    if(isAllowNull){
                        result.put(attribute.getColumn(),finalParam);
                    }
                }
            }
        }catch (NoSuchFieldException e){
            throw new ExecuteException("属性获取失败，请检查映射",e);
        }catch (IllegalAccessException e){
            throw new ExecuteException("属性不可访问",e);
        }
    }

    public static String getWriteParamStr( List<Attributes> attributes,Map<String,Object> paramMap,XContentBuilder xContentBuilder) throws Exception{
        if(xContentBuilder != null){
            xContentBuilder.startObject();
            for(Map.Entry<String,Object> entry:paramMap.entrySet()){
                xContentBuilder.field(entry.getKey(),entry.getValue());
            }
            xContentBuilder.endObject();
        }
        return JSON.toJSONString(paramMap);
    }

    public static String getWriteParamStr(boolean isAllowNull,Mapper mapper, Object bean) {
        return getWriteParamStr(isAllowNull,mapper,bean,null);
    }

    public static String getWriteParamStr(boolean isAllowNull,Mapper mapper, Object bean, XContentBuilder xContentBuilder){
        if(mapper instanceof CommonTypeMapper){
            CommonTypeMapper commonTypeMapper = (CommonTypeMapper)mapper;
            List<Attributes> attributes = commonTypeMapper.getAttributes();
            Map<String,Object> mapping = null;
            if(!mapper.isHaveNestedProperty()){
                mapping = beanToMap(((CommonTypeMapper) mapper).getAttributes(),bean,isAllowNull);
            }else{
                mapping = beanToFlatMap(((CommonTypeMapper) mapper).getAttributes(),bean,isAllowNull);
            }
            try{
                return getWriteParamStr(attributes,mapping,xContentBuilder);
            }catch (Exception e){
                throw new ExecuteException("参数解析失败");
            }
        }else{
            throw new ConfigException("写入eagle参数，mapper不能是内置类型");
        }
    }

    private static String assembleNestedFieldPrefix(String currentPrefix,Field field){
        String finalPrefix = null;
        if(currentPrefix == null){
            finalPrefix = field.getName();
        }else{
            finalPrefix = currentPrefix+"."+field.getName();
        }
        return finalPrefix;
    }

    private static List<Field> getAllFieldOfBean(Class clazz){
        List<Field> fields = new LinkedList<>();
        Class tempClass = clazz;
        while(tempClass != null && !tempClass.getName().equals("java.lang.Object")){
            Field[] tempFields = tempClass.getDeclaredFields();
            for(Field tempField:tempFields){
                fields.add(tempField);
            }
            tempClass = tempClass.getSuperclass();
        }
        return fields;
    }

    private static Map<String,Field> getFieldMapOfBean(Class clazz){
        Map<String,Field> fieldMap = new HashMap<>();
        Class tempClass = clazz;
        while(tempClass != null && !tempClass.getName().equals("java.lang.Object")){
            Field[] tempFields = tempClass.getDeclaredFields();
            for(Field tempField:tempFields){
                fieldMap.put(tempField.getName(),tempField);
            }
            tempClass = tempClass.getSuperclass();
        }
        return fieldMap;
    }

    public static InsertTypeParam parseInsertTypeParam(Method method,Object[] objects){
        Parameter[] parameters = method.getParameters();
        if(parameters.length > 0){
            InsertTypeParam insertTypeParam = new InsertTypeParam();
            for(int i=0;i<parameters.length;i++){
                Annotation[] parameterAnnotations = parameters[i].getDeclaredAnnotations();
                if(parameterAnnotations.length > 0){
                    for(Annotation annotation:parameterAnnotations){
                         if(annotation instanceof Key){
                             Object keyObj = objects[i];
                             if(keyObj instanceof java.lang.String){
                                 insertTypeParam.setKey((String)keyObj);
                             }else{
                                 throw new ParamParseExcetion("key 必须是java.lang.String类型");
                             }
                         }else if(annotation instanceof com.framework.db.core.parse.annotation.parameter.Parameter){
                             insertTypeParam.setParamObject(objects[i]);
                         }
                    }
                }
            }
            return insertTypeParam;
        }else{
            throw new ParamParseExcetion("insert类型的操作不能没有参数");
        }
    }

    public static UpdateByKeyParam parseUpdateByKeyTypeParam(Method method, Object[] objects){
        Parameter[] parameters = method.getParameters();
        if(parameters.length > 0){
            UpdateByKeyParam updateByKeyParam = new UpdateByKeyParam();
            for(int i=0;i<parameters.length;i++){
                Annotation[] parameterAnnotations = parameters[i].getDeclaredAnnotations();
                if(parameterAnnotations.length > 0){
                    for(Annotation annotation:parameterAnnotations){
                        if(annotation instanceof Key){
                            Object keyObj = objects[i];
                            if(keyObj instanceof java.lang.String){
                                updateByKeyParam.setKey((String)keyObj);
                            }else{
                                throw new ParamParseExcetion("key 必须是java.lang.String类型");
                            }
                        }else if(annotation instanceof com.framework.db.core.parse.annotation.parameter.Parameter){
                            updateByKeyParam.setParamObject(objects[i]);
                        }
                    }
                }
            }
            return updateByKeyParam;
        }else{
            throw new ParamParseExcetion("updateByKey类型的操作不能没有参数");
        }
    }

    public static DeleteByKeyParam parseDeleteByKeyTypeParam(Method method, Object[] objects){
        Parameter[] parameters = method.getParameters();
        if(parameters.length > 0){
            DeleteByKeyParam deleteByKeyParam = new DeleteByKeyParam();
            for(int i=0;i<parameters.length;i++){
                Annotation[] parameterAnnotations = parameters[i].getDeclaredAnnotations();
                if(parameterAnnotations.length > 0){
                    for(Annotation annotation:parameterAnnotations){
                        if(annotation instanceof Key){
                            Object keyObj = objects[i];
                            if(keyObj instanceof java.lang.String){
                                deleteByKeyParam.setKey((String)keyObj);
                            }else{
                                throw new ParamParseExcetion("key 必须是java.lang.String类型");
                            }
                        }
                    }
                }
            }
            return deleteByKeyParam;
        }else{
            throw new ParamParseExcetion("insert类型的操作不能没有参数");
        }
    }

    public static DeleteByQueryParam parseDeleteByQueryTypeParam(Method method, Object[] objects){
        Parameter[] parameters = method.getParameters();
        if(parameters.length > 0){
            DeleteByQueryParam deleteByQueryParam = new DeleteByQueryParam();
            for(int i=0;i<parameters.length;i++){
                Annotation[] parameterAnnotations = parameters[i].getDeclaredAnnotations();
                if(parameterAnnotations.length > 0){
                    for(Annotation annotation:parameterAnnotations){
                        if(annotation instanceof Query){
                            Object queryBuilderObj = objects[i];
                            if(queryBuilderObj instanceof QueryBuilder){
                                deleteByQueryParam.setQueryBuilder((QueryBuilder) queryBuilderObj);
                            }else{
                                throw new ParamParseExcetion("Query 必须是 org.elasticsearch.index.query.QueryBuilder类型");
                            }
                        }
                    }
                }
            }
            return deleteByQueryParam;
        }else{
            throw new ParamParseExcetion("deleteByQuery类型的操作不能没有参数");
        }
    }

    public static SelectTypeParam parseSelectTypeParam(Method method, Object[] objects){
        Parameter[] parameters = method.getParameters();
        if(parameters.length > 0){
            SelectTypeParam selectTypeParam = new SelectTypeParam();
            for(int i=0;i<parameters.length;i++){
                Annotation[] parameterAnnotations = parameters[i].getDeclaredAnnotations();
                if(parameterAnnotations.length > 0){
                    for(Annotation annotation:parameterAnnotations){
                        if(annotation instanceof Query){
                            Object queryBuilderObj = objects[i];
                            if(queryBuilderObj instanceof QueryBuilder){
                                selectTypeParam.setQueryBuilder((QueryBuilder) queryBuilderObj);
                            }else{
                                throw new ParamParseExcetion("Query 必须是 org.elasticsearch.index.query.QueryBuilder类型");
                            }
                        }
                    }
                }
            }
            return selectTypeParam;
        }else{
            throw new ParamParseExcetion("select类型的操作不能没有参数");
        }
    }

    public static SqlSelectTypeParam parseSqlSelectTypeParam(Method method,Object[] objects){
        Parameter[] parameters = method.getParameters();
        SqlSelectTypeParam sqlSelectTypeParam = new SqlSelectTypeParam();
        Map<String,Object> params = new HashMap<>();
        sqlSelectTypeParam.setParams(params);
        if(parameters.length > 0){
            for(int i=0;i<parameters.length;i++){
                Annotation[] parameterAnnotations = parameters[i].getDeclaredAnnotations();
                if(parameterAnnotations.length > 0){
                    for(Annotation annotation:parameterAnnotations){
                        if(annotation instanceof com.framework.db.core.parse.annotation.parameter.Parameter){
                            String paramName = ((com.framework.db.core.parse.annotation.parameter.Parameter)annotation).value();
                            if(StringUtils.isEmpty(paramName)){
                                throw new RuntimeException("查询类型是SqlSelectType时，注解parameter的value必须配置");
                            }else{
                                params.put(paramName,objects[i]);
                            }
                        }
                    }
                }else{
                    Field[] fields = objects[i].getClass().getDeclaredFields();
                    for(Field field:fields){
                        field.setAccessible(true);
                        try{
                           params.put(field.getName(),field.get(objects[i]));
                        }catch (Exception e){
                            throw  new ParamParseExcetion(e);
                        }
                    }
                }
            }
        }
        return sqlSelectTypeParam;
    }


}
