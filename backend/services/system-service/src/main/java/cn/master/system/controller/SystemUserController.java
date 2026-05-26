package cn.master.system.controller;

import cn.master.constants.OperationLogType;
import cn.master.dto.BasePageRequest;
import cn.master.dto.BatchProcessDTO;
import cn.master.dto.BatchProcessResponse;
import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.UserSelectOption;
import cn.master.system.dto.user.*;
import cn.master.system.entity.SystemUser;
import cn.master.system.log.annotation.Log;
import cn.master.system.log.service.UserLogService;
import cn.master.system.service.BaseUserRoleService;
import cn.master.system.service.SystemUserService;
import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户 控制层。
 *
 * @author 11's papa
 * @since 2026-05-12
 */
@RestController
@Tag(name = "用户接口")
@RequestMapping("/system/user")
public class SystemUserController {

    private final SystemUserService systemUserService;
    private final BaseUserRoleService baseUserRoleService;

    public SystemUserController(SystemUserService systemUserService,
                                @Qualifier("baseUserRoleService") BaseUserRoleService baseUserRoleService) {
        this.systemUserService = systemUserService;
        this.baseUserRoleService = baseUserRoleService;
    }

    @PostMapping("save")
    @Operation(summary = "系统设置-系统-用户-添加用户")
    public UserBatchCreateResponse save(@RequestBody @Parameter(description = "用户") @Validated({Created.class}) UserBatchCreateRequest request) {
        return systemUserService.addUser(request, SecurityUtils.getUserId());
    }

    @PostMapping("remove")
    @Operation(summary = "系统设置-系统-用户-删除用户")
    @Log(type = OperationLogType.DELETE, expression = "#clazz.deleteLog(#request)", clazz = UserLogService.class)
    public BatchProcessResponse remove(@Validated @RequestBody BatchProcessDTO request) {
        return systemUserService.deleteUser(request, SecurityUtils.getUserId(), SecurityUtils.getUsername());
    }

    @PostMapping("update")
    @Operation(summary = "系统设置-系统-用户-修改用户")
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.updateLog(#request)", clazz = UserLogService.class)
    public UserEditRequest update(@RequestBody @Validated({Updated.class}) UserEditRequest request) {
        return systemUserService.updateUser(request, SecurityUtils.getUserId());
    }

    @PostMapping("/update/enable")
    @Operation(summary = "系统设置-系统-用户-启用/禁用用户")
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.batchUpdateEnableLog(#request)", clazz = UserLogService.class)
    public BatchProcessResponse updateUserEnable(@Validated @RequestBody UserChangeEnableRequest request) {
        return systemUserService.updateUserEnable(request, SecurityUtils.getUserId(), SecurityUtils.getUsername());
    }

    /**
     * 查询所有用户。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    @Operation(description = "查询所有用户")
    public List<SystemUser> list() {
        return systemUserService.list();
    }

    /**
     * 根据主键获取用户。
     *
     * @param id 用户主键
     * @return 用户详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取用户")
    public SystemUser getInfo(@PathVariable @Parameter(description = "用户主键") String id) {
        return systemUserService.getById(id);
    }

    @PostMapping("page")
    @Operation(description = "分页查询用户")
    public Page<UserTableVO> page(@Validated @RequestBody BasePageRequest request) {
        return systemUserService.getUserPage(request);
    }

    @GetMapping("/get/global/system/role")
    @Operation(summary = "系统设置-系统-用户-查找系统级用户组")
    public List<UserSelectOption> getGlobalSystemRole() {
        return baseUserRoleService.getGlobalSystemRoleList();
    }
}
