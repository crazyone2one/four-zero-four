package cn.master.quartz.service;

import cn.master.quartz.config.ClusterQuartzJobBean;
import cn.master.quartz.config.FixedDelayJobListener;
import cn.master.quartz.util.JobDetailTrigger;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
public class QuartzManageService {
    @Resource
    private Scheduler scheduler;

    public List<JobExecutionContext> getCurrentlyExecutingJobs() throws Exception {
        return scheduler.getCurrentlyExecutingJobs();
    }

    public void deleteJob(JobKey jobKey) throws Exception {
        scheduler.deleteJob(jobKey);
    }

    public void pauseJob(JobKey jobKey) throws Exception {
        scheduler.pauseJob(jobKey);
    }

    public void pauseTrigger(TriggerKey triggerKey) throws Exception {
        scheduler.pauseTrigger(triggerKey);
    }

    public void unscheduleJob(TriggerKey triggerKey) throws Exception {
        scheduler.unscheduleJob(triggerKey);
    }

    public void resumeJob(JobKey jobKey) throws Exception {
        scheduler.resumeJob(jobKey);
    }

    public void rescheduleJob(TriggerKey triggerKey, Trigger newTrigger) throws Exception {
        scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    public boolean checkExists(TriggerKey triggerKey) throws Exception {
        return scheduler.checkExists(triggerKey);
    }

    public boolean checkExists(JobKey jobKey) throws Exception {
        return scheduler.checkExists(jobKey);
    }

    public JobKey getJobKey(TriggerKey triggerKey) throws Exception {
        return scheduler.getTrigger(triggerKey).getJobKey();
    }

    public void addJob(JobDetail jobDetail, Trigger trigger) throws Exception {
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void addJob(JobDataMap jobDataMap, Trigger trigger) throws Exception {
        JobDetail jobDetail = JobBuilder.newJob(ClusterQuartzJobBean.class)
                .storeDurably(true).usingJobData(jobDataMap).build();
        addJob(jobDetail, trigger);
    }

    public void addJob(String beanName, String methodName, Trigger trigger, Object... params) throws Exception {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("targetObject", beanName);
        jobDataMap.put("targetMethod", methodName);
        jobDataMap.put("params", params);
        addJob(jobDataMap, trigger);
    }

    public void addJob(String beanName, String methodName, String cron, Object... params) throws Exception {
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(beanName + "." + methodName)
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();
        addJob(beanName, methodName, trigger, params);
    }

    public List<? extends Trigger> getTriggersOfJob(JobKey jobKey) throws Exception {
        return scheduler.getTriggersOfJob(jobKey);
    }

    public Trigger getTrigger(TriggerKey triggerKey) throws Exception {
        return scheduler.getTrigger(triggerKey);
    }


    @Transactional(rollbackFor = Exception.class)
    public void rescheduleJobs(List<JobKey> jobs, List<TriggerKey> triggers, Map<String, JobDetailTrigger> jobDetailTriggerMap) throws Exception {
        scheduler.deleteJobs(jobs);
        scheduler.unscheduleJobs(triggers);
        scheduler.getListenerManager().addJobListener(new FixedDelayJobListener());
        for (String jobDetailIdentity : jobDetailTriggerMap.keySet()) {
            JobDetailTrigger jobDetailTrigger = jobDetailTriggerMap.get(jobDetailIdentity);
            scheduler.scheduleJob(jobDetailTrigger.getJobDetail(), jobDetailTrigger.getTrigger());
        }
    }

}
