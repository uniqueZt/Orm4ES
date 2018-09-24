package com.framework.db.core.filter;

/**
 * Created by zhangteng on 2018/9/24.
 */
public interface Filter {

    public FilterResult beforeExecute(OperateEntity entity);

    public void afterExecute(OperateEntity entity,Object executeResult);

}
