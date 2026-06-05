package cn.master.system.service;

import cn.master.dto.BasePageRequest;
import cn.master.system.dto.ScheduleConfig;
import cn.master.system.dto.TableBatchProcessDTO;
import cn.master.system.dto.taskhub.ScheduleRequest;
import cn.master.system.dto.taskhub.TaskHubScheduleDTO;
import cn.master.system.entity.Schedule;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

import java.util.List;

/**
 * 定时任务 服务层。
 *
 * @author 11's papa
 * @since 2026-05-22
 */
public interface ScheduleService extends IService<Schedule> {
    void addSchedule(Schedule schedule, String userId);

    void editSchedule(Schedule schedule);

    Page<TaskHubScheduleDTO> getSchedulePage(BasePageRequest request);

    Schedule checkScheduleExit(String id);

    void enable(String id, String userId, String path, String module);

    void scheduleBatchOperation(TableBatchProcessDTO request, String userId, String projectId, String path, String module, boolean enable, List<String> projectIds);

    void addOrUpdateCronJob(Schedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz);

    String scheduleConfig(ScheduleConfig scheduleConfig, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz, String operator);


    void updateCron(ScheduleRequest request, String userId, String path, String module);
}
