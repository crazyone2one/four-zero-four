package cn.master.system.service.impl;

import cn.master.constants.UserRoleScope;
import cn.master.constants.UserRoleType;
import cn.master.exception.FZFException;
import cn.master.system.dto.permission.PermissionCache;
import cn.master.system.dto.permission.PermissionDefinitionItem;
import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.entity.UserRole;
import cn.master.system.service.BaseUserRolePermissionService;
import cn.master.system.service.GlobalUserRoleService;
import cn.master.system.service.UserRoleRelationService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.master.constants.InternalUserRole.MEMBER;
import static cn.master.result.SystemResultCode.GLOBAL_USER_ROLE_EXIST;
import static cn.master.result.SystemResultCode.GLOBAL_USER_ROLE_PERMISSION;
import static cn.master.system.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * @author : 11's papa
 * @since : 2026/5/25, 星期一
 **/
@Service("globalUserRoleService")
public class GlobalUserRoleServiceImpl extends BaseUserRoleServiceImpl implements GlobalUserRoleService {


    public GlobalUserRoleServiceImpl(@Qualifier("baseUserRolePermissionService") BaseUserRolePermissionService baseUserRolePermissionService,
                                     PermissionCache permissionCache,
                                     UserRoleRelationService userRoleRelationService) {
        super(baseUserRolePermissionService, permissionCache, userRoleRelationService);
    }

    @Override
    public List<UserRole> list() {
        List<UserRole> userRoles = queryChain().where(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)).list();
        // 先按照类型排序，再按照创建时间排序
        userRoles.sort(Comparator.comparingInt(this::getTypeOrder)
                .thenComparingInt(item -> getInternal(item.getInternal()))
                .thenComparing(UserRole::getCreateTime));
        return userRoles;
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(String id) {
        UserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        return getPermissionSetting(userRole);
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        UserRole userRole = getWithCheck(request.getUserRoleId());
        checkGlobalUserRole(userRole);
        // 内置管理员级别用户组无法更改权限
        checkAdminUserRole(userRole);
        super.updatePermissionSetting(request);
    }

    @Override
    public UserRole add(UserRole userRole) {
        userRole.setInternal(false);
        userRole.setScopeId(UserRoleScope.GLOBAL);
        checkExist(userRole);
        return super.add(userRole);
    }

    @Override
    public UserRole updateUserRole(UserRole userRole) {
        UserRole originUserRole = getWithCheck(userRole.getId());
        checkGlobalUserRole(originUserRole);
        checkInternalUserRole(originUserRole);
        userRole.setInternal(false);
        checkExist(userRole);
        return super.update(userRole);
    }

    @Override
    public void delete(String id, String userId) {
        UserRole userRole = getWithCheck(id);
        checkGlobalUserRole(userRole);
        super.delete(userRole, MEMBER.getValue(), userId, UserRoleScope.SYSTEM);
    }

    private void checkExist(UserRole userRole) {
        if (StringUtils.isBlank(userRole.getName())) {
            return;
        }
        boolean exists = queryChain().where(USER_ROLE.NAME.eq(userRole.getName()).and(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL))
                .and(USER_ROLE.ID.ne(userRole.getId()))).exists();
        if (exists) {
            throw new FZFException(GLOBAL_USER_ROLE_EXIST);
        }
    }

    @Override
    public void checkGlobalUserRole(UserRole userRole) {
        if (!UserRoleScope.GLOBAL.equals(userRole.getScopeId())) {
            throw new FZFException(GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    private int getInternal(Boolean internal) {
        return BooleanUtils.isTrue(internal) ? 0 : 1;
    }

    private int getTypeOrder(UserRole userRole) {
        Map<String, Integer> typeOrderMap = new HashMap<>(3) {{
            put(UserRoleType.SYSTEM.name(), 1);
            put(UserRoleType.ORGANIZATION.name(), 2);
            put(UserRoleType.PROJECT.name(), 3);
        }};
        return typeOrderMap.getOrDefault(userRole.getType(), 0);
    }
}
