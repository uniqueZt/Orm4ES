package com.framework.db.core.util;

import com.alibaba.fastjson.JSON;
import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.exception.ParamParseExcetion;
import com.framework.db.core.model.mapper.Attributes;
import com.framework.db.core.model.param.*;
import com.framework.db.core.parse.annotation.parameter.Key;
import com.framework.db.core.parse.annotation.parameter.Query;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    public static boolean isLeaf(Map<String,Attributes> attributesMap,String fieldName,Class instanceClass){
         if(leafSet.contains(instanceClass)){
             return true;
         }
         for(Class clazz:leafSet){
             if(clazz.isAssignableFrom(instanceClass)){
                 return true;
             }
         }
         if(null != attributesMap && null != fieldName){
             Attributes attributes = attributesMap.get(fieldName);
             if(null != attributes && attributes.isJson()){
                 return true;
             }
         }
         return false;
    }

    public static Map<String,Object> beanToFlatMap(Map<String,Attributes> attributesMap,Object bean){
        Map<String,Object> result = new HashMap<>();
        try{
            beanToMapInternal(attributesMap,bean,null,result);
        }catch (IllegalAccessException e){
            throw new ExecuteException("参数解析错误",e);
        }
        return result;
    }

    public static Map<String,Object> beanToFlatMap(List<Attributes> attributes,Object bean){
        Map<String,Attributes> attributesMap = new HashMap<>();
        for(Attributes attribute:attributes){
          attributesMap.put(attribute.getProperty(),attribute);
        }
        return beanToFlatMap(attributesMap,bean);
    }

    public static String getWriteParamStr( List<Attributes> attributes,Map<String,Object> flatMap){
        Map<String,Object> writeParam = new HashMap<>();
        for(Attributes attribute:attributes){
            if(flatMap.containsKey(attribute.getProperty())){
                Object writeValue = flatMap.get(attribute.getProperty());
                if(attribute.isJson()){
                    writeValue = JSON.toJSONString(writeValue);
                }
                writeParam.put(attribute.getColumn(),writeValue);
            }
        }
        return JSON.toJSONString(writeParam);
    }

    public static String getWriteParamStr( List<Attributes> attributes,Map<String,Object> flatMap,XContentBuilder xContentBuilder) throws Exception{
        Map<String,Object> writeParam = new HashMap<>();
        xContentBuilder.startObject();
        for(Attributes attribute:attributes){
            if(flatMap.containsKey(attribute.getProperty())){
                Object writeValue = flatMap.get(attribute.getProperty());
                if(attribute.isJson()){
                    writeValue = JSON.toJSONString(writeValue);
                }
                writeParam.put(attribute.getColumn(),writeValue);
                xContentBuilder.field(attribute.getColumn(),writeValue);
            }
        }
        xContentBuilder.endObject();
        return JSON.toJSONString(writeParam);
    }

    public static String getWriteParamStr( List<Attributes> attributes,Object bean){
        Map<String,Object> flatMap = beanToFlatMap(attributes,bean);
        return getWriteParamStr(attributes,flatMap);
    }

    public static String getWriteParamStr(List<Attributes> attributes, Object bean, XContentBuilder xContentBuilder){
        Map<String,Object> flatMap = beanToFlatMap(attributes,bean);
        try{
            return getWriteParamStr(attributes,flatMap,xContentBuilder);
        }catch (Exception e){
            throw new ExecuteException("参数解析失败");
        }
    }



    private static void beanToMapInternal(Map<String,Attributes> attributesMap,Object bean,String prefix,Map<String,Object> result) throws IllegalAccessException{
        Class clazz = bean.getClass();
        if(isLeaf(attributesMap,null,clazz)){
            return;
        }
        List<Field> fields = getAllFieldOfBean(clazz);
        for(Field field:fields){
            field.setAccessible(true);
            if(isLeaf(attributesMap,field.getName(),field.getType())){
                result.put(assembleNestedFieldPrefix(prefix,field),field.get(bean));
            }else{
                Object fieldValue = field.get(bean);
                if(null != fieldValue){
                    beanToMapInternal(attributesMap,fieldValue,assembleNestedFieldPrefix(prefix,field),result);
                }else{
                    result.put(assembleNestedFieldPrefix(prefix,field),null);
                }
            }
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
