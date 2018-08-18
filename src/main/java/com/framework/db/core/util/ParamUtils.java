package com.framework.db.core.util;

import com.framework.db.core.exception.ParamParseExcetion;
import com.framework.db.core.model.param.*;
import com.framework.db.core.parse.annotation.parameter.Key;
import com.framework.db.core.parse.annotation.parameter.Query;
import org.elasticsearch.index.query.QueryBuilder;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class ParamUtils {

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
