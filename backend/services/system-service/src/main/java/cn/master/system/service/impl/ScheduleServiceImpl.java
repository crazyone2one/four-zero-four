package cn.master.system.service.impl;

import cn.master.constants.HttpMethodConstants;
import cn.master.constants.OperationLogType;
import cn.master.dto.BasePageRequest;
import cn.master.exception.FZFException;
import cn.master.system.dto.TableBatchProcessDTO;
import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.taskhub.TaskHubScheduleDTO;
import cn.master.system.entity.Project;
import cn.master.system.entity.Schedule;
import cn.master.system.handler.ScheduleManager;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.log.dto.LogDTOBuilder;
import cn.master.system.mapper.ScheduleMapper;
import cn.master.system.service.OperationLogService;
import cn.master.system.service.ScheduleService;
import cn.master.util.LogUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.system.entity.table.OrganizationTableDef.ORGANIZATION;
import static cn.master.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.system.entity.table.ScheduleTableDef.SCHEDULE;

/**
 * 定时任务 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-22
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {
    private final OperationLogService operationLogService;
    private final ScheduleManager scheduleManager;

    @Override
    public Page<TaskHubScheduleDTO> getSchedulePage(BasePageRequest request, List<String> projectIds) {
        Page<TaskHubScheduleDTO> page = queryChain()
                .select(SCHEDULE.ID, SCHEDULE.PROJECT_ID, SCHEDULE.RESOURCE_TYPE, SCHEDULE.VALUE, SCHEDULE.ENABLE)
                .select(SCHEDULE.NAME, PROJECT.NAME.as("projectName"), PROJECT.NUM.as("resourceNum"))
                .select("QRTZ_TRIGGERS.PREV_FIRE_TIME").as("last_time")
                .select("QRTZ_TRIGGERS.NEXT_FIRE_TIME").as("nextTime")
                .from(SCHEDULE)
                .leftJoin(PROJECT).on(SCHEDULE.PROJECT_ID.eq(PROJECT.ID))
                .leftJoin("QRTZ_TRIGGERS").on(SCHEDULE.JOB.eq("QRTZ_TRIGGERS.JOB_GROUP").and(SCHEDULE.KEY.eq("QRTZ_TRIGGERS.TRIGGER_NAME")))
                .where(SCHEDULE.PROJECT_ID.in(projectIds)
                        .and(SCHEDULE.NAME.like(request.getKeyword())
                                .or(SCHEDULE.NUM.like(request.getKeyword()))))
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), TaskHubScheduleDTO.class);
        processTaskCenterSchedule(page, projectIds);
        return page;
    }

    @Override
    public Schedule checkScheduleExit(String id) {
        return queryChain().where(SCHEDULE.ID.eq(id)).oneOpt()
                .orElseThrow(() -> new FZFException(Translator.get("schedule_not_exist")));
    }

    @Override
    public void enable(String id, String userId, String path, String module) {
        Schedule schedule = checkScheduleExit(id);
        schedule.setEnable(!schedule.getEnable());
        mapper.update(schedule);
        try {
            addOrUpdateCronJob(schedule, new JobKey(schedule.getKey(), schedule.getJob()),
                    new TriggerKey(schedule.getKey(), schedule.getJob()), (Class<? extends Job>) Class.forName(schedule.getJob()));
        } catch (ClassNotFoundException e) {
            LogUtils.error(e);
            throw new RuntimeException(e);
        }
        saveLog(List.of(schedule), userId, path, HttpMethodConstants.GET.name(), module, OperationLogType.UPDATE.name());
    }

    @Override
    public void scheduleBatchOperation(TableBatchProcessDTO request, String userId, String projectId, String path, String module, boolean enable, List<String> projectIds) {

    }

    private void saveLog(List<Schedule> scheduleList, String userId, String path, String method, String module, String type) {
        List<String> projectIds = scheduleList.stream().map(Schedule::getProjectId).distinct().toList();
        List<ProjectDTO> orgList = getOrgListByProjectIds(projectIds);
        Map<String, String> orgMap = orgList.stream().collect(Collectors.toMap(ProjectDTO::getId, ProjectDTO::getOrganizationId));
        List<LogDTO> logs = new ArrayList<>();
        scheduleList.forEach(s -> {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(s.getProjectId())
                    .organizationId(orgMap.get(s.getProjectId()))
                    .type(type)
                    .module(module)
                    .method(method)
                    .path(path)
                    .sourceId(s.getResourceId())
                    .content(s.getName())
                    .createUser(userId)
                    .build().getLogDTO();
            logs.add(dto);
        });
        operationLogService.batchAdd(logs);
    }

    private List<ProjectDTO> getOrgListByProjectIds(List<String> projectIds) {
        return QueryChain.of(Project.class)
                .select(PROJECT.ID, ORGANIZATION.ID.as("organizationId"))
                .from(PROJECT)
                .innerJoin(ORGANIZATION).on(PROJECT.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                .where(PROJECT.ID.in(projectIds))
                .listAs(ProjectDTO.class);
    }

    @Override
    public void addOrUpdateCronJob(Schedule request, JobKey jobKey, TriggerKey triggerKey, Class<? extends Job> clazz) {
        Boolean enable = request.getEnable();
        String cronExpression = request.getValue();
        if (BooleanUtils.isTrue(enable) && StringUtils.isNotBlank(cronExpression)) {
            try {
                scheduleManager.addOrUpdateCronJob(jobKey, triggerKey, clazz, cronExpression,
                        scheduleManager.getDefaultJobDataMap(request, cronExpression, request.getCreateUser()));
            } catch (SchedulerException e) {
                throw new FZFException("定时任务开启异常: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                scheduleManager.removeJob(jobKey, triggerKey);
            } catch (Exception e) {
                throw new FZFException("定时任务关闭异常: " + e.getMessage());
            }
        }
    }

    private void processTaskCenterSchedule(Page<TaskHubScheduleDTO> page, List<String> projectIds) {

    }
}
