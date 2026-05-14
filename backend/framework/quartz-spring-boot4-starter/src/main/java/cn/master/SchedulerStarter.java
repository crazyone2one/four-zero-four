package cn.master;

import cn.master.autoconfigure.QuartzProperties;
import cn.master.quartz.anno.QuartzScheduled;
import cn.master.quartz.config.ClusterQuartzFixedDelayJobBean;
import cn.master.quartz.config.ClusterQuartzJobBean;
import cn.master.quartz.config.FixedDelayJobData;
import cn.master.quartz.config.FixedDelayJobListener;
import cn.master.quartz.service.QuartzManageService;
import cn.master.quartz.util.JobDetailTrigger;
import jakarta.annotation.Resource;
import org.jspecify.annotations.Nullable;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.scheduling.SchedulingException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
public class SchedulerStarter implements BeanPostProcessor, ApplicationContextAware, ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(SchedulerStarter.class);

    private Instant now;
    @Resource
    private Scheduler scheduler;
    @Resource
    private TimeZone quartzTimeZone;
    @Resource
    private QuartzProperties quartzProperties;
    private final Map<String, JobDetailTrigger> jobDetailTriggerMap = new HashMap<>();

    private ConfigurableApplicationContext applicationContext;
    @Resource
    private QuartzManageService quartzManageService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        now = Instant.now();
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Override
    public @Nullable Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public @Nullable Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(bean.getClass());
        for (Method method : methods) {
            QuartzScheduled annotation = AnnotationUtils.findAnnotation(method, QuartzScheduled.class);
            if (annotation != null) {
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("targetObject", beanName);
                jobDataMap.put("targetMethod", method.getName());
                String cron = annotation.cron();
                long fixedDelay = annotation.fixedDelay();
                long fixedRate = annotation.fixedRate();
                int initialDelay = (int) annotation.initialDelay();
                final JobDetail jobDetail;
                final Trigger trigger;
                String jobDetailIdentity = beanName + "." + method.getName();
                if (cron != null && !cron.isEmpty()) {
                    cron = getCronExpression(cron);
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
                jobDetailTriggerMap.put(jobDetailIdentity, new JobDetailTrigger(jobDetail, trigger));
            }
        }
        return bean;
    }

    private String getCronExpression(String cron) {
        cron = cron.trim();
        if (cron.startsWith("${") && cron.endsWith("}")) {
            return applicationContext.getBeanFactory().resolveEmbeddedValue(cron);
        }
        return cron;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            if (!scheduler.isShutdown()) {
                // Not using the Quartz startDelayed method since we explicitly want a daemon
                // thread here, not keeping the JVM alive in case of all other threads ending.
                Thread schedulerThread = new Thread(() -> {
                    try {
                        QuartzProperties quartzProperties = this.applicationContext.getBean(QuartzProperties.class);
                        TimeUnit.SECONDS.sleep(quartzProperties.getStartupDelay().getSeconds());
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        // simply proceed
                    }
                    try {
                        // init all jobs
                        logger.info("reschedule all jobs...");
                        quartzManageService.rescheduleJobs(getJobKeys(), getTriggerKeys(), jobDetailTriggerMap);
                        scheduler.start();
                    } catch (Exception ex) {
                        throw new SchedulingException("Could not start Quartz Scheduler after delay", ex);
                    }
                });
                schedulerThread.setName("Quartz Scheduler [" + scheduler.getSchedulerName() + "]");
                schedulerThread.setDaemon(true);
                schedulerThread.start();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    private List<TriggerKey> getTriggerKeys() throws SchedulerException {
        return new ArrayList<>(scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals(quartzProperties.getGroupName())));
    }

    private List<JobKey> getJobKeys() throws SchedulerException {
        return new ArrayList<>(scheduler.getJobKeys(GroupMatcher.jobGroupEquals(quartzProperties.getGroupName())));
    }
}
