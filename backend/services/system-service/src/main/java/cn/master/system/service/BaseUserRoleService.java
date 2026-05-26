package cn.master.system.service;

import cn.master.system.dto.UserSelectOption;
import cn.master.system.dto.permission.PermissionDefinitionItem;
import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRoleRelation;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 用户组 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface BaseUserRoleService extends IService<UserRole> {
    void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem);

    List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations);

    List<UserSelectOption> getGlobalSystemRoleList();

    UserRole checkResourceExist(UserRole userRole);

    UserRole getWithCheck(String id);

    void checkGlobalUserRole(UserRole userRole);

    List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole);

    void checkAdminUserRole(UserRole userRole);

    void updatePermissionSetting(PermissionSettingUpdateRequest request);

    void checkInternalUserRole(UserRole userRole);

    UserRole add(UserRole userRole);

    UserRole update(UserRole userRole);

    void delete(UserRole userRole, String defaultRoleId, String currentUserId, String orgId);
}
