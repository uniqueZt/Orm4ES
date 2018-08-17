package com.framework.db.core.model.operate;

/**
 * Created by zhangteng on 2018/8/17.
 * 对应操作
 */
public class Operate {

    public final static String PARAMETER = "parameter";

    public final static String RESULT = "result";

    public final static String SIZE = "size";

    public final static String INDEX = "index";

    public final static String TYPE = "type";

    public final static String SCROLL = "scroll";

    public final static String TIME = "time";

    public final static String ID = "id";

    public final static String REFRESH = "refresh";
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
