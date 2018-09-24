package com.framework.db.core.model.param;

import java.util.List;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class BatchInsertTypeParam implements OperateParam{

    private List paramObjects;

    public List getParamObjects() {
        return paramObjects;
    }

    public void setParamObjects(List paramObjects) {
        this.paramObjects = paramObjects;
    }
}
