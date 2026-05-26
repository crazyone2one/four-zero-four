package cn.master.system.service.impl;

import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.entity.UserRolePermission;
import cn.master.system.mapper.UserRolePermissionMapper;
import cn.master.system.service.BaseUserRolePermissionService;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户组权限 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service("baseUserRolePermissionService")
public class BaseUserRolePermissionServiceImpl extends ServiceImpl<UserRolePermissionMapper, UserRolePermission> implements BaseUserRolePermissionService {

    @Override
    public List<UserRolePermission> getByRoleId(String roleId) {
        return queryChain().where(UserRolePermission::getRoleId).eq(roleId).list();
    }

    @Override
    public Set<String> getPermissionIdSetByRoleId(String roleId) {
        return getByRoleId(roleId).stream()
                .map(UserRolePermission::getPermissionId)
                .collect(Collectors.toSet());
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        List<PermissionSettingUpdateRequest.PermissionUpdateRequest> permissions = request.getPermissions();
        QueryChain<UserRolePermission> permissionQueryChain = queryChain().where(UserRolePermission::getRoleId).eq(request.getUserRoleId())
                .and(UserRolePermission::getPermissionId).ne("PROJECT_BASE_INFO:READ");
        mapper.deleteByQuery(permissionQueryChain);
        String groupId = request.getUserRoleId();
        permissions.forEach(permission -> {
            if (BooleanUtils.isTrue(permission.getEnable())) {
                String permissionId = permission.getId();
                UserRolePermission groupPermission = new UserRolePermission();
                groupPermission.setRoleId(groupId);
                groupPermission.setPermissionId(permissionId);
                mapper.insert(groupPermission);
            }
        });
    }
}
