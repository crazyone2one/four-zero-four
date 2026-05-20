package cn.master.system.controller;

import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectSwitchRequest;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.entity.Project;
import cn.master.system.service.ProjectService;
import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目 控制层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@RestController
@Tag(name = "项目接口")
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("save")
    @Operation(description = "保存项目")
    public ProjectDTO save(@RequestBody @Validated({Created.class}) UpdateProjectRequest request) {
        return projectService.add(request, SecurityUtils.getUserId());
    }

    /**
     * 根据主键删除项目。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除项目")
    public boolean remove(@PathVariable @Parameter(description = "项目主键") String id) {
        return projectService.removeById(id);
    }

    @PostMapping("update")
    @Operation(description = "根据主键更新项目")
    public ProjectDTO update(@RequestBody @Validated({Updated.class}) UpdateProjectRequest request) {
        return projectService.updateProject(request, SecurityUtils.getUserId());
    }

    /**
     * 查询所有项目。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有项目")
    public List<Project> list() {
        return projectService.list();
    }

    @GetMapping("/list/options/{organizationId}")
    @Operation(summary = "根据组织ID获取所有有权限的项目")
    public List<Project> getUserProject(@PathVariable String organizationId) {
        return projectService.getUserProject(organizationId, SecurityUtils.getUserId());
    }

    /**
     * 根据主键获取项目。
     *
     * @param id 项目主键
     * @return 项目详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取项目")
    public Project getInfo(@PathVariable @Parameter(description = "项目主键") String id) {
        return projectService.getById(id);
    }

    /**
     * 分页查询项目。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询项目")
    public Page<Project> page(@Parameter(description = "分页信息") Page<Project> page) {
        return projectService.page(page);
    }

    @PostMapping("/switch")
    @Operation(summary = "切换项目")
    public UserDTO switchProject(@RequestBody ProjectSwitchRequest request) {
        return projectService.switchProject(request, SecurityUtils.getUserId());
    }
}
