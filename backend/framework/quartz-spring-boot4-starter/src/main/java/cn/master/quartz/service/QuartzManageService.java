package cn.master.quartz.service;

import cn.master.autoconfigure.QuartzProperties;
import cn.master.quartz.anno.QuartzScheduled;
import cn.master.quartz.config.ClusterQuartzFixedDelayJobBean;
import cn.master.quartz.config.ClusterQuartzJobBean;
import cn.master.quartz.config.FixedDelayJobData;
import cn.master.quartz.config.FixedDelayJobListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class QuartzManageService {
    @Resource
    private Scheduler scheduler;
    @Resource
    private TimeZone quartzTimeZone;
    @Resource
    private QuartzProperties quartzProperties;
    private final Instant now = Instant.now();

    // private final Map<String, JobDetailTrigger> jobDetailTriggerMap = new HashMap<>();
    @Transactional(rollbackFor = Exception.class)
    public void addJobFromAnnotation(String jobName, String beanName, String cron, Object... params) {
        try {
            JobKey jobKey = new JobKey(jobName, quartzProperties.getGroupName());
            if (scheduler.checkExists(jobKey)) {
                throw new SchedulerException("任务 [" + jobName + "] 已存在");
            }
            Object targetBean = applicationContext.getBean(beanName);
            Class<?> targetBeanClass = targetBean.getClass();
            Optional<Method> targetMethodOpt = Arrays.stream(targetBeanClass.getMethods())
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
            addJob(jobDetail, trigger);
            // return targetBeanClass.getName();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改定时任务的Cron表达式
     * <p>
     * 该方法用于动态更新已存在的Cron触发器的执行时间规则。如果新的Cron表达式与当前触发器的表达式不同，
     * 则会创建一个新的触发器并重新调度任务。
     * </p>
     *
     * @param triggerKey 触发器标识，包含触发器名称和组名
     * @param cron       新的Cron表达式，用于定义任务的执行时间规则
     */
    public void modifyCronJobTime(TriggerKey triggerKey, String cron) {
        log.info("modifyCronJobTime: {},{}", triggerKey.getName(), triggerKey.getGroup());
        try {
            CronTrigger trigger = (CronTrigger) getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }

            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器
                triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组
                triggerBuilder.startNow(); // 立即执行
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)); // 触发器时间设定
                trigger = (CronTrigger) triggerBuilder.build(); // 创建Trigger对象
                rescheduleJob(triggerKey, trigger); // 修改一个任务的触发时间
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addOrUpdateCronJob(String jobName, String beanName, String cron, TriggerKey triggerKey, Object... params) {
        log.info("AddOrUpdateCronJob: {},{}", jobName, triggerKey.getGroup());
        try {
            if (checkExists(triggerKey)) {
                modifyCronJobTime(triggerKey, cron);
            } else {
                addJobFromAnnotation(jobName, beanName, cron, params);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
}
