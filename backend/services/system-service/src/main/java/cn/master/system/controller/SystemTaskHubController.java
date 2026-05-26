package cn.master.system.controller;

import cn.master.dto.BasePageRequest;
import cn.master.system.dto.taskhub.TaskHubScheduleDTO;
import cn.master.system.service.BaseTaskHubService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
@Tag(name = "系统任务中心")
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/task-center")
public class SystemTaskHubController {
    private final BaseTaskHubService baseTaskHubService;

    @PostMapping("/schedule/page")
    @Operation(summary = "系统-任务中心-后台执行任务列表")
    public Page<TaskHubScheduleDTO> scheduleList(@Validated @RequestBody BasePageRequest request) {
        return baseTaskHubService.getSchedulePage(request, null);
    }
}
