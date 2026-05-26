package cn.master.system.service;

import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.entity.UserRolePermission;
import com.mybatisflex.core.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 用户组权限 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface BaseUserRolePermissionService extends IService<UserRolePermission> {
    List<UserRolePermission> getByRoleId(String roleId);

    Set<String> getPermissionIdSetByRoleId(String roleId);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);
}
