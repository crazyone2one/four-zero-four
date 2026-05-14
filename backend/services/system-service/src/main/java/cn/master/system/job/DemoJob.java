package cn.master.system.job;

import cn.master.quartz.anno.QuartzScheduled;
import org.springframework.stereotype.Component;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@Component
public class DemoJob {

    @QuartzScheduled(cron = "0/5 * * * * ?")
    public void execute() {
        System.out.println("DemoJob execute");
    }
}
