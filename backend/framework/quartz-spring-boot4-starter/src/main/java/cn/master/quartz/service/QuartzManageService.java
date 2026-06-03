package cn.master.quartz.service;

import cn.master.autoconfigure.QuartzProperties;
import cn.master.quartz.anno.QuartzScheduled;
import cn.master.quartz.config.ClusterQuartzFixedDelayJobBean;
import cn.master.quartz.config.ClusterQuartzJobBean;
import cn.master.quartz.config.FixedDelayJobData;
import cn.master.quartz.config.FixedDelayJobListener;
import cn.master.quartz.util.JobDetailTrigger;
import jakarta.annotation.Resource;
import org.quartz.*;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;

import static cn.master.quartz.util.QuartzBeanFactory.applicationContext;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
public class QuartzManageService {
    @Resource
    private Scheduler scheduler;
    @Resource
    private TimeZone quartzTimeZone;
    @Resource
    private QuartzProperties quartzProperties;
    private final Instant now = Instant.now();
    // private final Map<String, JobDetailTrigger> jobDetailTriggerMap = new HashMap<>();

    public void addJobFromAnnotation(String jobName, String beanName, String cron, Object... params) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, quartzProperties.getGroupName());
        if (scheduler.checkExists(jobKey)) {
            throw new SchedulerException("任务 [" + jobName + "] 已存在");
        }
        Object targetBean = applicationContext.getBean(beanName);
        Optional<Method> targetMethodOpt = Arrays.stream(targetBean.getClass().getMethods())
                .filter(method -> AnnotatedElementUtils.hasAnnotation(method, QuartzScheduled.class))
                .filter(method -> {
                    QuartzScheduled annotation = method.getAnnotation(QuartzScheduled.class);
                    return annotation.name().equals(jobName) && annotation.group().equals(quartzProperties.getGroupName());
                }).findFirst();
        if (targetMethodOpt.isEmpty()) {
            throw new SchedulerException("No matching @QuartzScheduled annotation method in bean [" + beanName + "]");
        }
        Method targetMethod = targetMethodOpt.get();
        QuartzScheduled annotation = targetMethod.getAnnotation(QuartzScheduled.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("targetObject", beanName);
        jobDataMap.put("targetMethod", targetMethod.getName());
        jobDataMap.put("params", params);

        // String cron = annotation.cron();
        long fixedDelay = annotation.fixedDelay();
        long fixedRate = annotation.fixedRate();
        int initialDelay = (int) annotation.initialDelay();
        final JobDetail jobDetail;
        final Trigger trigger;
        String jobDetailIdentity = beanName + "." + jobName;
        if (cron != null && !cron.isEmpty()) {
            jobDetail = JobBuilder.newJob(ClusterQuartzJobBean.class)
                    .storeDurably(true).usingJobData(jobDataMap).build();
            trigger = TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(jobDetailIdentity, quartzProperties.getGroupName()))
                    .startAt(new Date(now.plusMillis(initialDelay).toEpochMilli()))
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron).inTimeZone(quartzTimeZone))
                    .build();
        } else if (fixedDelay > 0) {
            jobDataMap.put(FixedDelayJobListener.FIXED_DELAY_JOB_DATA, new FixedDelayJobData(fixedDelay));
            jobDetail = JobBuilder.newJob(ClusterQuartzFixedDelayJobBean.class)
                    .storeDurably(true).usingJobData(jobDataMap).build();
            trigger = TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(jobDetailIdentity, quartzProperties.getGroupName()))
                    .startAt(new Date(now.plusMillis(initialDelay).toEpochMilli()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(fixedDelay).repeatForever())
                    .build();
        } else {
            jobDetail = JobBuilder.newJob(ClusterQuartzJobBean.class)
                    .storeDurably(true).usingJobData(jobDataMap).build();
            trigger = TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(jobDetailIdentity, quartzProperties.getGroupName()))
                    .startAt(new Date(now.plusMillis(initialDelay).toEpochMilli()))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withIntervalInMilliseconds(fixedRate).repeatForever())
                    .build();
        }
        // jobDetailTriggerMap.put(jobDetailIdentity, new JobDetailTrigger(jobDetail, trigger));
        scheduler.scheduleJob(jobDetail, trigger);
    }

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
