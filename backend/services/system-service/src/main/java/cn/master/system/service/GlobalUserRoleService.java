package cn.master.system.service;

import cn.master.system.dto.permission.PermissionDefinitionItem;
import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.entity.UserRole;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/25, 星期一
 **/
public interface GlobalUserRoleService extends BaseUserRoleService {
    List<UserRole> list();

    List<PermissionDefinitionItem> getPermissionSetting(String id);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    UserRole add(UserRole userRole);

    UserRole updateUserRole(UserRole userRole);

    void delete(String id, String userId);
}
