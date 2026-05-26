package cn.master.system.job;

import cn.master.system.handler.BaseScheduleJob;
import cn.master.util.LogUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
public class CustomJob extends BaseScheduleJob {
    @Override
    protected void businessExecute(JobExecutionContext context) {
        config.keySet().forEach(key -> LogUtils.info("key: " + key + " value: " + config.get(key)));
        LogUtils.info("run CustomJob at: " + LocalDateTime.now());
    }

    public static JobKey getJobKey(String id) {
        return new JobKey(id, CustomJob.class.getName());
    }

    public static TriggerKey getTriggerKey(String id) {
        return new TriggerKey(id, CustomJob.class.getName());
    }
}
