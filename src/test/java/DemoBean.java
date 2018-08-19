/**
 * Created by zhangteng on 2018/8/18.
 */
public class DemoBean {

    private String logId;

    private String logContent;

    private String actionName;

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
