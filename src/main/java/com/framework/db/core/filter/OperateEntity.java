package com.framework.db.core.filter;

import com.framework.db.core.model.mapper.Mapper;
import com.framework.db.core.model.operate.Operate;
import com.framework.db.core.model.param.OperateParam;


/**
 * Created by zhangteng on 2018/9/24.
 */
public class OperateEntity {

    private OperateType operateType;

    private Class<?> namespaceClass;

    private String mappingId;

    private Operate operate;

    private OperateParam operateParam;

    private Mapper mapper;

    public OperateType getOperateType() {
        return operateType;
    }

    public void setOperateType(OperateType operateType) {
        this.operateType = operateType;
    }

    public Class<?> getNamespaceClass() {
        return namespaceClass;
    }

    public void setNamespaceClass(Class<?> namespaceClass) {
        this.namespaceClass = namespaceClass;
    }

    public String getMappingId() {
        return mappingId;
    }

    public void setMappingId(String mappingId) {
        this.mappingId = mappingId;
    }

    public Operate getOperate() {
        return operate;
    }

    public void setOperate(Operate operate) {
        this.operate = operate;
    }

    public OperateParam getOperateParam() {
        return operateParam;
    }

    public void setOperateParam(OperateParam operateParam) {
        this.operateParam = operateParam;
    }

    public Mapper getMapper() {
        return mapper;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }
}
