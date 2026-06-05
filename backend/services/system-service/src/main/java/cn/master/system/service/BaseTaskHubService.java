package cn.master.system.service;

import cn.master.dto.BasePageRequest;
import cn.master.system.dto.taskhub.TaskHubScheduleDTO;
import cn.master.system.entity.Schedule;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.system.entity.table.ScheduleTableDef.SCHEDULE;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
@Service
public class BaseTaskHubService {
    public Page<TaskHubScheduleDTO> getSchedulePage(BasePageRequest request, List<String> projectIds) {
        Page<TaskHubScheduleDTO> page = QueryChain.of(Schedule.class)
                .select(SCHEDULE.ID, SCHEDULE.PROJECT_ID, SCHEDULE.RESOURCE_TYPE, SCHEDULE.VALUE, SCHEDULE.ENABLE)
                .select(SCHEDULE.NUM, SCHEDULE.CREATE_TIME, SCHEDULE.CREATE_USER)
                .select(SCHEDULE.NAME, PROJECT.NAME.as("projectName"), PROJECT.NUM.as("resourceNum"))
                .select("QRTZ_TRIGGERS.PREV_FIRE_TIME AS last_time")
                .select("QRTZ_TRIGGERS.NEXT_FIRE_TIME AS nextTime")
                .from(SCHEDULE.as("s"))
                .leftJoin(PROJECT).on(SCHEDULE.PROJECT_ID.eq(PROJECT.ID))
                .leftJoin("QRTZ_TRIGGERS").on("CONCAT(s.job, '.', s.executor_handler) = QRTZ_TRIGGERS.TRIGGER_NAME")
                .where(SCHEDULE.PROJECT_ID.in(projectIds)
                        .and(SCHEDULE.NAME.like(request.getKeyword())
                                .or(SCHEDULE.NUM.like(request.getKeyword()))))
                .orderBy(SCHEDULE.ENABLE.desc(), SCHEDULE.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), TaskHubScheduleDTO.class);
        processTaskCenterSchedule(page, projectIds);
        return page;
    }

    private void processTaskCenterSchedule(Page<TaskHubScheduleDTO> page, List<String> projectIds) {

    }
}
