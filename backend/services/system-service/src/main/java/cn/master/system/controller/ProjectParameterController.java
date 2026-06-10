package cn.master.system.controller;

import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.request.ProjectParamsRequest;
import cn.master.system.entity.ProjectParameter;
import cn.master.system.service.ProjectParameterService;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目级参数 控制层。
 *
 * @author 11's papa
 * @since 2026-06-10
 */
@RestController
@Tag(name = "项目级参数接口")
@RequiredArgsConstructor
@RequestMapping("/project/parameter")
public class ProjectParameterController {

    private final ProjectParameterService projectParameterService;

    @PostMapping("save")
    @Operation(description = "保存项目级参数")
    public ProjectParameter save(@RequestBody @Parameter(description = "项目级参数") ProjectParamsRequest request) {
        return projectParameterService.add(request, SecurityUtils.getUserId());
    }

    /**
     * 根据主键删除项目级参数。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    @Operation(description = "根据主键删除项目级参数")
    public boolean remove(@PathVariable @Parameter(description = "项目级参数主键") String id) {
        return projectParameterService.removeById(id);
    }

    @PostMapping("update")
    @Operation(description = "根据主键更新项目级参数")
    public ProjectParameter update(@RequestBody @Parameter(description = "项目级参数主键") ProjectParamsRequest request) {
        return projectParameterService.updateParam(request, SecurityUtils.getUserId());
    }

    /**
     * 查询所有项目级参数。
     *
     * @return 所有数据
     */
    @GetMapping("list/{projectId}")
    @Operation(description = "查询所有项目级参数")
    public List<ProjectParameter> list(@PathVariable String projectId) {
        return projectParameterService.listByProjectId(projectId);
    }

    /**
     * 根据主键获取项目级参数。
     *
     * @param id 项目级参数主键
     * @return 项目级参数详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取项目级参数")
    public ProjectParameter getInfo(@PathVariable @Parameter(description = "项目级参数主键") String id) {
        return projectParameterService.getById(id);
    }

    /**
     * 分页查询项目级参数。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    @Operation(description = "分页查询项目级参数")
    public Page<ProjectParameter> page(@Parameter(description = "分页信息") Page<ProjectParameter> page) {
        return projectParameterService.page(page);
    }

}
