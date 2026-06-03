package cn.master.system.job;

import cn.master.quartz.anno.QuartzScheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@Component
public class DemoJob {

    @QuartzScheduled(name = "job1")
    public void demoJob1() {
        System.out.println("demoJob1 execute");
    }

    @QuartzScheduled(name = "job2")
    public void demoJob2(Map<String, Object> params) {
        System.out.println("demoJob2 execute==>" + params);
    }

    @QuartzScheduled(name = "job3")
    public void demoJob3(Map<String, Object> params) {
        System.out.println("demoJob3 execute==>" + params);
    }
}
