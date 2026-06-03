package cn.master.system.handler;

import cn.master.exception.FZFException;
import cn.master.quartz.service.QuartzManageService;
import cn.master.system.entity.Schedule;
import cn.master.util.LogUtils;
import jakarta.annotation.Resource;
import org.quartz.*;

/**
 * @author : 11's papa
 * @since : 2026/5/22, 星期五
 **/
public class ScheduleManager {
    @Resource
    private QuartzManageService quartzManageService;

    public void addCronJob(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass, String cron, JobDataMap jobDataMap) {
        try {
            LogUtils.info("addCronJob: " + triggerKey.getName() + "," + triggerKey.getGroup());
            JobBuilder jobBuilder = JobBuilder.newJob(jobClass).withIdentity(jobKey);
            if (jobDataMap != null) {
                jobBuilder.usingJobData(jobDataMap);
            }

            TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
            triggerBuilder.withIdentity(triggerKey);
            triggerBuilder.startNow();
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
            CronTrigger trigger = (CronTrigger) triggerBuilder.build();
            quartzManageService.addJob(jobBuilder.build(), trigger);

        } catch (Exception e) {
            LogUtils.error(e);
            throw new FZFException("定时任务配置异常: " + e.getMessage());
        }
    }

    public void modifyCronJobTime(TriggerKey triggerKey, String cron) {
        LogUtils.info("modifyCronJobTime: " + triggerKey.getName() + "," + triggerKey.getGroup());
        try {
            CronTrigger trigger = (CronTrigger) quartzManageService.getTrigger(triggerKey);
            if (trigger == null) {
                return;
            }

            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                /* 方式一 ：调用 rescheduleJob 开始 */
                TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();// 触发器
                triggerBuilder.withIdentity(triggerKey);// 触发器名,触发器组
                triggerBuilder.startNow(); // 立即执行
                triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron)); // 触发器时间设定
                trigger = (CronTrigger) triggerBuilder.build(); // 创建Trigger对象
                quartzManageService.rescheduleJob(triggerKey, trigger); // 修改一个任务的触发时间
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void removeJob(JobKey jobKey, TriggerKey triggerKey) {
        try {
            LogUtils.info("RemoveJob: " + jobKey.getName() + "," + jobKey.getGroup());
            quartzManageService.pauseTrigger(triggerKey);
            quartzManageService.unscheduleJob(triggerKey);
            quartzManageService.deleteJob(jobKey);
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void addOrUpdateCronJob(JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> jobClass, String cron, JobDataMap jobDataMap)
            throws Exception {
        LogUtils.info("AddOrUpdateCronJob: " + jobKey.getName() + "," + triggerKey.getGroup());
        if (quartzManageService.checkExists(triggerKey)) {
            modifyCronJobTime(triggerKey, cron);
        } else {
            addCronJob(jobKey, triggerKey, jobClass, cron, jobDataMap);
        }
    }

    public JobDataMap getDefaultJobDataMap(Schedule schedule, String expression, String userId) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("resourceId", schedule.getExecutorHandler());
        jobDataMap.put("expression", expression);
        jobDataMap.put("userId", userId);
        jobDataMap.put("config", schedule.getConfig());
        jobDataMap.put("projectId", schedule.getProjectId());
        return jobDataMap;
    }
}
