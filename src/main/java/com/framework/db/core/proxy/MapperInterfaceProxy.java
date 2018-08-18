package com.framework.db.core.proxy;

import com.framework.db.core.exception.ExecuteException;
import com.framework.db.core.exception.InternalException;
import com.framework.db.core.middle.ElasticSearchCallSupport;
import com.framework.db.core.model.mapper.CommonTypeMapper;
import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.namespace.Namespace;
import com.framework.db.core.model.operate.*;
import com.framework.db.core.model.param.*;
import com.framework.db.core.util.ParamUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class MapperInterfaceProxy<T> implements MethodInterceptor {

    private ElasticSearchCallSupport elasticSearchCallSupport;

    private Namespace<T> namespace;

    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        String mappingId = method.getName();
        Operate operate = namespace.getOperateMap().get(mappingId);
        if(null == operate){
            throw new InternalException("method:"+mappingId+"没有配置对应operate请检查");
        }
        if(operate instanceof InsertTypeOperate){
            insertOperateTypeProxy(method,objects);
        }else if(operate instanceof UpdateByKeyTypeOperate){
            updateOperateTypeProxy(method,objects);
        }else if(operate instanceof DeleteByKeyTypeOperate){
            deleteByKeyOperateTypeProxy(method,objects);
        }else if(operate instanceof DeleteByQueryTypeOperate){
            deleteByQueryOperateTypeProxy(method,objects);
        }else if(operate instanceof SelectTypeOperate){
            return selectOperateTypeProxy(method,objects);
        }else if(operate instanceof SqlSelectTypeOperate){
            return sqlSelectOperateTypeProxy(method,objects);
        }
        return null;
    }

    private List<?> sqlSelectOperateTypeProxy(Method method,Object[] objects){
        String mappingId = method.getName();
        SqlSelectTypeOperate sqlSelectTypeOperate = (SqlSelectTypeOperate)namespace.getOperateMap().get(mappingId);
        Mapper mapper = namespace.getMapperMap().get(sqlSelectTypeOperate.getResultName());
        SqlSelectTypeParam sqlSelectTypeParam = ParamUtils.parseSqlSelectTypeParam(method,objects);
        return elasticSearchCallSupport.sqlSelect(sqlSelectTypeParam.getParams(),sqlSelectTypeOperate,mapper);
    }

    private List<?> selectOperateTypeProxy(Method method,Object[] objects){
        String mappingId = method.getName();
        SelectTypeOperate selectTypeOperate = (SelectTypeOperate)namespace.getOperateMap().get(mappingId);
        Mapper mapper = namespace.getMapperMap().get(selectTypeOperate.getResultName());
        SelectTypeParam selectTypeParam = ParamUtils.parseSelectTypeParam(method,objects);
        if(selectTypeParam.getQueryBuilder() == null){
            throw new ExecuteException("select类型的操作，query不能为空");
        }
        return elasticSearchCallSupport.select(selectTypeParam.getQueryBuilder(),selectTypeOperate,mapper);
    }

    private void deleteByQueryOperateTypeProxy(Method method,Object[] objects){
        String mappingId = method.getName();
        DeleteByQueryTypeOperate deleteByQueryTypeOperate = (DeleteByQueryTypeOperate)namespace.getOperateMap().get(mappingId);
        DeleteByQueryParam deleteByQueryParam = ParamUtils.parseDeleteByQueryTypeParam(method,objects);
        if(deleteByQueryParam.getQueryBuilder() == null){
            throw new ExecuteException("deleteByQuery操作query不能为空");
        }
        elasticSearchCallSupport.deleteByQuery(deleteByQueryParam.getQueryBuilder(),deleteByQueryTypeOperate);
    }

    private void deleteByKeyOperateTypeProxy(Method method,Object[] objects){
        String mappingId = method.getName();
        DeleteByKeyTypeOperate deleteByKeyTypeOperate = (DeleteByKeyTypeOperate)namespace.getOperateMap().get(mappingId);
        DeleteByKeyParam deleteByKeyParam = ParamUtils.parseDeleteByKeyTypeParam(method,objects);
        if(deleteByKeyParam.getKey() == null){
            throw new ExecuteException("deleteByKey操作key不能为空");
        }
        elasticSearchCallSupport.deleteByKey(deleteByKeyParam.getKey(),deleteByKeyTypeOperate);
    }

    private void insertOperateTypeProxy(Method method,Object[] objects){
        String mappingId = method.getName();
        InsertTypeOperate insertTypeOperate = (InsertTypeOperate) namespace.getOperateMap().get(mappingId);
        CommonTypeMapper commonTypeMapper = (CommonTypeMapper) namespace.getMapperMap().get(insertTypeOperate.getParameterName());
        InsertTypeParam insertTypeParam = ParamUtils.parseInsertTypeParam(method,objects);
        if(null != insertTypeParam.getKey()) {
            elasticSearchCallSupport.insert(insertTypeParam.getKey(), insertTypeParam.getParamObject(), insertTypeOperate, commonTypeMapper);
        }else{
            elasticSearchCallSupport.insert(insertTypeParam.getParamObject(),insertTypeOperate,commonTypeMapper);
        }
    }

    private void updateOperateTypeProxy(Method method,Object[] objects){
        String mappingId = method.getName();
        UpdateByKeyTypeOperate updateByKeyTypeOperate = (UpdateByKeyTypeOperate)namespace.getOperateMap().get(mappingId);
        CommonTypeMapper commonTypeMapper = (CommonTypeMapper) namespace.getMapperMap().get(updateByKeyTypeOperate.getParameterName());
        UpdateByKeyParam updateByKeyParam = ParamUtils.parseUpdateByKeyTypeParam(method,objects);
        if(updateByKeyParam.getKey()==null || updateByKeyParam.getParamObject() == null){
            throw new ExecuteException("updateByKey类操作，key和parameter 均不能为空");
        }
        elasticSearchCallSupport.updateByKey(updateByKeyParam.getKey(),updateByKeyParam.getParamObject(),updateByKeyTypeOperate,commonTypeMapper);
    }

    public  T createProxy(){
        if(null == namespace){
            throw new InternalException("namespace 没有初始化");
        }
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(namespace.getNamespaceClass());
        enhancer.setCallback(this);
        return (T)enhancer.create();
    }

    public ElasticSearchCallSupport getElasticSearchCallSupport() {
        return elasticSearchCallSupport;
    }

    public void setElasticSearchCallSupport(ElasticSearchCallSupport elasticSearchCallSupport) {
        this.elasticSearchCallSupport = elasticSearchCallSupport;
    }

    public Namespace getNamespace() {
        return namespace;
    }

    public MapperInterfaceProxy setNamespace(Namespace namespace) {
        this.namespace = namespace;
        return this;
    }
}
