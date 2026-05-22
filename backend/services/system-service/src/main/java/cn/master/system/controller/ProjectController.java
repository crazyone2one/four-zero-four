package cn.master.system.controller;

import cn.master.constants.OperationLogType;
import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.ProjectRequest;
import cn.master.system.dto.project.UpdateProjectNameRequest;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectAddMemberRequest;
import cn.master.system.dto.request.ProjectMemberRequest;
import cn.master.system.dto.request.ProjectSwitchRequest;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.dto.user.UserExtendDTO;
import cn.master.system.entity.Project;
import cn.master.system.entity.SystemUser;
import cn.master.system.log.annotation.Log;
import cn.master.system.log.service.SystemProjectLogService;
import cn.master.system.service.ProjectService;
import cn.master.system.service.SimpleUserService;
import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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
@RequestMapping("/system/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final SimpleUserService simpleUserService;

    @PostMapping("save")
    @Operation(description = "保存项目")
    @Log(type = OperationLogType.ADD, expression = "#clazz.addLog(#request)", clazz = SystemProjectLogService.class)
    public ProjectDTO save(@RequestBody @Validated({Created.class}) UpdateProjectRequest request) {
        return projectService.add(request, SecurityUtils.getUserId());
    }

    @GetMapping("remove/{id}")
    @Operation(description = "根据主键删除项目")
    @Log(type = OperationLogType.DELETE, expression = "#clazz.deleteLog(#id)", clazz = SystemProjectLogService.class)
    public void remove(@PathVariable @Parameter(description = "项目主键") String id) {
        projectService.delete(id, SecurityUtils.getUserId());
    }

    @PostMapping("update")
    @Operation(description = "根据主键更新项目")
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.updateLog(#request)", clazz = SystemProjectLogService.class)
    public ProjectDTO update(@RequestBody @Validated({Updated.class}) UpdateProjectRequest request) {
        return projectService.updateProject(request, SecurityUtils.getUserId());
    }

    @PostMapping("/rename")
    @Operation(summary = "系统设置-系统-组织与项目-项目-修改项目名称")
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.renameLog(#request)", clazz = SystemProjectLogService.class)
    public void rename(@RequestBody @Validated({Updated.class}) UpdateProjectNameRequest request) {
        projectService.rename(request, SecurityUtils.getUserId());
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

    @PostMapping("page")
    @Operation(description = "分页查询项目")
    public Page<ProjectDTO> page(@Validated @RequestBody ProjectRequest request) {
        return projectService.pageProject(request);
    }

    @PostMapping("/switch")
    @Operation(summary = "切换项目")
    public UserDTO switchProject(@RequestBody ProjectSwitchRequest request) {
        return projectService.switchProject(request, SecurityUtils.getUserId());
    }

    @GetMapping("/enable/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-启用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.updateLog(#id)", clazz = SystemProjectLogService.class)
    public void enable(@PathVariable String id) {
        projectService.enable(id, SecurityUtils.getUserId());
    }

    @GetMapping("/disable/{id}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-禁用")
    @Parameter(name = "id", description = "项目ID", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.updateLog(#id)", clazz = SystemProjectLogService.class)
    public void disable(@PathVariable String id) {
        projectService.disable(id, SecurityUtils.getUserId());
    }

    @PostMapping("/add-member")
    @Operation(summary = "系统设置-系统-组织与项目-项目-添加成员")
    public void addProjectMember(@Validated @RequestBody ProjectAddMemberRequest request) {
        projectService.addMemberByProject(request, SecurityUtils.getUserId());
    }

    @PostMapping("/member-list")
    @Operation(summary = "系统设置-系统-组织与项目-项目-成员列表")
    public Page<UserExtendDTO> getProjectMember(@Validated @RequestBody ProjectMemberRequest request) {
        return projectService.getProjectMember(request);
    }

    @GetMapping("/remove-member/{projectId}/{userId}")
    @Operation(summary = "系统设置-系统-组织与项目-项目-移除成员")
    @Parameter(name = "userId", description = "用户id", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    @Parameter(name = "projectId", description = "项目id", schema = @Schema(requiredMode = Schema.RequiredMode.REQUIRED))
    public int removeProjectMember(@PathVariable String projectId, @PathVariable String userId) {
        return projectService.removeProjectMember(projectId, userId, SecurityUtils.getUserId());
    }

    @GetMapping("/user-list")
    @Operation(summary = "系统设置-系统-组织与项目-项目-系统-组织及项目, 获取管理员下拉选项")
    public List<SystemUser> getUserList(@Schema(description = "查询关键字，根据邮箱和用户名查询")
                                        @RequestParam(value = "keyword", required = false) String keyword) {
        return simpleUserService.getUserList(keyword);
    }
}
