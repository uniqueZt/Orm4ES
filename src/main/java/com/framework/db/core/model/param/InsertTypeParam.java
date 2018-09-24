package com.framework.db.core.model.param;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class InsertTypeParam implements OperateParam{
    private String key;

    private Object paramObject;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getParamObject() {
        return paramObject;
    }

    public void setParamObject(Object paramObject) {
        this.paramObject = paramObject;
    }
}
