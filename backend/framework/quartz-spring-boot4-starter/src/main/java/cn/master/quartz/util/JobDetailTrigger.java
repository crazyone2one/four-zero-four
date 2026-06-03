package cn.master.quartz.util;

import lombok.Getter;
import org.quartz.JobDetail;
import org.quartz.Trigger;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/

@Getter
public class JobDetailTrigger {
    JobDetail jobDetail;
    Trigger trigger;

    public JobDetailTrigger(JobDetail jobDetail, Trigger trigger) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
    }

}
