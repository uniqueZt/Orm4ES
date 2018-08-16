package com.framework.db.core.model.operate;

import com.framework.db.core.model.operate.Operate;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class InsertTypeOperate extends Operate{

    //索引名称
    private String index;

    //类型名称
    private String type;

    //写入刷新策略
    private RefreshType refresh;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RefreshType getRefresh() {
        return refresh;
    }

    public void setRefresh(RefreshType refresh) {
        this.refresh = refresh;
    }
}
