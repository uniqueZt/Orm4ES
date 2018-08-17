package com.framework.db.core.model.operate;

/**
 * Created by zhangteng on 2018/8/17.
 */
public class UpdateByKeyTypeOperate extends Operate{

    private String index;

    private String type;

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
