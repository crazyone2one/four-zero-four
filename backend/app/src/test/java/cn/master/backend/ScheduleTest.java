package cn.master.backend;

import cn.master.constants.OperationLogModule;
import cn.master.constants.ScheduleResourceType;
import cn.master.constants.ScheduleType;
import cn.master.system.dto.ScheduleConfig;
import cn.master.system.entity.Schedule;
import cn.master.system.job.CustomJob;
import cn.master.system.service.ScheduleService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.quartz.JobKey;
import org.quartz.TriggerKey;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
class ScheduleTest {
    @Resource
    ScheduleService scheduleService;
    public static final String DEFAULT_PROJECT_ID = "100001100001";

    @Test
    void testAddSchedule() {
        Schedule schedule = new Schedule();
        schedule.setName("job1");
        schedule.setExecutorHandler("job1");
        schedule.setEnable(true);
        schedule.setValue("0 0/1 * * * ?");
        schedule.setKey("demoJob");
        schedule.setCreateUser("admin");
        schedule.setProjectId(DEFAULT_PROJECT_ID);
        schedule.setConfig(Map.of("k1", "v1", "k2", 123, "k3", true));
        schedule.setJob("demoJob");
        schedule.setType(ScheduleType.CRON.name());
        schedule.setResourceType(ScheduleResourceType.DEMO_INFO.name());
        scheduleService.addSchedule(schedule);
    }

    @Test
    void testUpdateSchedule() {
        Schedule schedule = scheduleService.getById("99368416983000185");
        schedule.setEnable(false);
        scheduleService.editSchedule(schedule);
    }

    @Test
    void testEnableSchedule() {
        scheduleService.enable("99368416983000185", "admin", "/system/task-center/schedule/switch/", OperationLogModule.SETTING_SYSTEM_TASK_CENTER);
    }

    @Test
    void testScheduleConfig() {
        Schedule schedule = scheduleService.getById("99368416983000185");
        Map<String, Object> config = schedule.getConfig();
        config.put("k3", true);
        ScheduleConfig scheduleConfig = ScheduleConfig.builder().enable(true).resourceId(schedule.getExecutorHandler()).projectId(schedule.getProjectId())
                .name(schedule.getName()).cron(schedule.getValue())
                .resourceType(schedule.getResourceType()).config(config).build();
        JobKey jobKey = CustomJob.getJobKey(schedule.getKey());
        TriggerKey triggerKey = CustomJob.getTriggerKey(schedule.getKey());
        scheduleService.scheduleConfig(scheduleConfig, jobKey, triggerKey, CustomJob.class, "admin");
    }
}
