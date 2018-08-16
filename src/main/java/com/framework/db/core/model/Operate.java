package com.framework.db.core.model;

/**
 * Created by zhangteng on 2018/8/17.
 * 对应操作
 */
public class Operate {

    //参数对应的mapper名称
    private String parameterName;

    //返回结果对应的mapper名称
    private String resultName;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }
}
