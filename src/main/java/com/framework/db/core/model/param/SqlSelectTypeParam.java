package com.framework.db.core.model.param;

import java.util.Map;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class SqlSelectTypeParam implements OperateParam{

    private Map<String,Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
