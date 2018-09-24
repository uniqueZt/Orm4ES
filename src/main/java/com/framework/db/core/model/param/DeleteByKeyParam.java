package com.framework.db.core.model.param;

/**
 * Created by zhangteng on 2018/8/18.
 */
public class DeleteByKeyParam implements OperateParam{

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
