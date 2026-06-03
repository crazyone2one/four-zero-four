package cn.master.system.dto;

import cn.master.constants.ScheduleType;
import cn.master.system.entity.Schedule;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
@Data
@Builder
public class ScheduleConfig {
    private String resourceId;

    private String key;

    private String projectId;

    private String name;

    private Boolean enable;

    private String cron;

    private String resourceType;

    private Map<String, Object> config;

    public Schedule genCronSchedule(Schedule schedule) {
        if (schedule == null) {
            schedule = new Schedule();
        }
        schedule.setName(this.getName());
        schedule.setExecutorHandler(this.getResourceId());
        schedule.setType(ScheduleType.CRON.name());
        schedule.setKey(this.getKey());
        schedule.setEnable(this.getEnable());
        schedule.setProjectId(this.getProjectId());
        schedule.setValue(this.getCron());
        schedule.setResourceType(this.getResourceType());
        schedule.setConfig(this.getConfig());
        return schedule;
    }
}
