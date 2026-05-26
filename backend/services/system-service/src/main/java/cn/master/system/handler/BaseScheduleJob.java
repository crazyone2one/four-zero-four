package cn.master.system.handler;

import cn.master.util.JsonUtils;
import cn.master.util.LogUtils;
import org.quartz.*;
import tools.jackson.core.type.TypeReference;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
public abstract class BaseScheduleJob implements Job {
    protected String resourceId;

    protected String userId;

    protected String expression;
    protected Map<String, Object> config;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getTrigger().getJobKey();
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        this.resourceId = jobDataMap.getString("resourceId");
        this.userId = jobDataMap.getString("userId");
        this.expression = jobDataMap.getString("expression");
        this.config = JsonUtils.objectToType(new TypeReference<Map<String, Object>>() {
        }).apply(jobDataMap.get("config"));

        LogUtils.info(jobKey.getGroup() + " Running: " + resourceId);
        businessExecute(context);
    }

    protected abstract void businessExecute(JobExecutionContext context);
}
