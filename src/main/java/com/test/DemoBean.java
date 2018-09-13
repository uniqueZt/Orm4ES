package com.test;

import com.framework.db.core.parse.annotation.config.mapper.Attribute;
import com.framework.db.core.parse.annotation.config.mapper.Mapper;

/**
 * Created by zhangteng on 2018/8/18.
 */
@Mapper(namespace = DemoMapperTest.class,name = "demoBean")
public class DemoBean {

    @Attribute(column = "log_id")
    public String logId;

    @Attribute(column = "log_content")
    private String logContent;

    @Attribute(column = "action_name")
    private String actionName;

    @Attribute(column = "create_time")
    private String createTime;

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getLogContent() {
        return logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DemoBean{" +
                "logId='" + logId + '\'' +
                ", logContent='" + logContent + '\'' +
                ", actionName='" + actionName + '\'' +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
