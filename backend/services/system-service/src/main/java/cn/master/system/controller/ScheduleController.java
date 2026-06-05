package cn.master.system.controller;

import cn.master.constants.OperationLogModule;
import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.taskhub.ScheduleRequest;
import cn.master.system.entity.Schedule;
import cn.master.system.service.ScheduleService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 定时任务 控制层。
 *
 * @author 11's papa
 * @since 2026-05-22
 */
@RestController
@Tag(name = "定时任务接口")
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 保存定时任务。
     *
     * @param schedule 定时任务
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    @Operation(description = "保存定时任务")
    public boolean save(@RequestBody @Parameter(description = "定时任务") Schedule schedule) {
        return scheduleService.save(schedule);
    }

    /**
     * 根据主键删除定时任务。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除定时任务")
    public boolean remove(@PathVariable @Parameter(description = "定时任务主键") String id) {
        return scheduleService.removeById(id);
    }

    /**
     * 根据主键更新定时任务。
     *
     * @param schedule 定时任务
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    @Operation(description = "根据主键更新定时任务")
    public boolean update(@RequestBody @Parameter(description = "定时任务主键") Schedule schedule) {
        return scheduleService.updateById(schedule);
    }

    /**
     * 查询所有定时任务。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有定时任务")
    public List<Schedule> list() {
        return scheduleService.list();
    }

    /**
     * 根据主键获取定时任务。
     *
     * @param id 定时任务主键
     * @return 定时任务详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取定时任务")
    public Schedule getInfo(@PathVariable @Parameter(description = "定时任务主键") String id) {
        return scheduleService.getById(id);
    }

    /**
     * 分页查询定时任务。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询定时任务")
    public Page<Schedule> page(@Parameter(description = "分页信息") Page<Schedule> page) {
        return scheduleService.page(page);
    }

    @GetMapping("/switch/{id}")
    @Operation(summary = "系统-任务中心-后台任务开启关闭")
    public void enable(@PathVariable String id) {
        scheduleService.enable(id, SecurityUtils.getUserId(), "/system/task-center/schedule/switch/", OperationLogModule.SETTING_SYSTEM_TASK_CENTER);
    }

    @PostMapping("/update-cron")
    @Operation(summary = "系统-任务中心-后台任务更新cron表达式")
    public void updateValue(@Validated @RequestBody ScheduleRequest request) {
        scheduleService.updateCron(request, SecurityUtils.getUserId(), "/system/task-center/schedule/update-cron", OperationLogModule.SETTING_SYSTEM_TASK_CENTER);
    }
}
