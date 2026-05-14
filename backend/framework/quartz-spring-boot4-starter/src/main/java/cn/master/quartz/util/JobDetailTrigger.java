package cn.master.quartz.util;

import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/

public class JobDetailTrigger {
    JobDetail jobDetail;
    Trigger trigger;

    public JobDetailTrigger(JobDetail jobDetail, Trigger trigger) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
    }

    public JobDetail getJobDetail() {
        return jobDetail;
    }

    public Trigger getTrigger() {
        return trigger;
    }
}
