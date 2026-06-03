package cn.master.quartz.config;

import org.quartz.*;
import org.quartz.listeners.JobListenerSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
public final class FixedDelayJobListener extends JobListenerSupport {
    private static final String FIXED_JOB_LISTENER_NAME = "FixedDelayJobListener";

    public static final String FIXED_DELAY_JOB_DATA = "FIXED_DELAY_JOB_DATA";

    @Override
    public String getName() {
        return FIXED_JOB_LISTENER_NAME;
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        JobDetail jobdetail = context.getJobDetail();
        JobDataMap data = jobdetail.getJobDataMap();
        if (!data.containsKey(FIXED_DELAY_JOB_DATA)) {
            getLog().debug("Not a fixed delay job : {}", context.getJobDetail().getKey());
            return;
        }
        if (shouldNotSchedule(context)) {
            getLog().debug("Not scheduling {} again as there is still an unfired trigger.", context.getJobDetail().getKey());
            return;
        } else {
            getLog().debug("Rescheduling {} as there is no unfired trigger.", context.getJobDetail().getKey());
        }

        TriggerKey oldTriggerKey = context.getTrigger().getKey();
        FixedDelayJobData jobData = (FixedDelayJobData) data.getWrappedMap().get(FIXED_DELAY_JOB_DATA);
        Trigger newTrigger = buildNewTrigger(jobData, oldTriggerKey);

        rescheduleJob(context.getScheduler(), oldTriggerKey, newTrigger);
    }

    private boolean shouldNotSchedule(JobExecutionContext context) {
        List<? extends Trigger> triggersOfJob = getTriggersOfJob(context);
        for (Trigger trigger : triggersOfJob) {
            if (trigger instanceof SimpleTrigger && ((SimpleTrigger) trigger).getTimesTriggered() == 0) {
                return true;
            }
        }
        return false;
    }

    private List<? extends Trigger> getTriggersOfJob(final JobExecutionContext context) {
        List<? extends Trigger> triggersOfJob = new ArrayList<>();

        try {
            triggersOfJob = context.getScheduler().getTriggersOfJob(context.getJobDetail().getKey());
        } catch (SchedulerException e) {
            getLog().error(e.getMessage());
        }
        return triggersOfJob;
    }

    private Trigger buildNewTrigger(FixedDelayJobData jobData, TriggerKey oldTriggerKey) {
        Date runTime = jobData.getNextScheduleDate();
        return newTrigger().withIdentity(oldTriggerKey).startAt(runTime).build();
    }

    private void rescheduleJob(Scheduler scheduler, TriggerKey oldTriggerKey, Trigger newTrigger) {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.rescheduleJob(oldTriggerKey, newTrigger);
            }
        } catch (SchedulerException se) {
            getLog().error("failed to reschedule the job with trigger : {}", oldTriggerKey, se);
        }
    }
}
