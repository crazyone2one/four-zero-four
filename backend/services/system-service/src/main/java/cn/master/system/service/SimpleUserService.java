package cn.master.system.service;

import cn.master.system.dto.user.UserDTO;
import cn.master.system.dto.user.UserRolePermissionDTO;
import cn.master.system.dto.user.UserRoleResourceDTO;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRolePermission;
import cn.master.system.entity.UserRoleRelation;
import com.mybatisflex.core.query.QueryChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
@Service
public class SimpleUserService {

    public UserDTO getUser(String userId) {
        UserDTO userDTO = QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).oneAs(UserDTO.class);
        if (Objects.isNull(userDTO)) {
            return null;
        }
        if (BooleanUtils.isFalse(userDTO.getEnable())) {
            throw new DisabledException("");
        }
        UserRolePermissionDTO dto = getUserRolePermission(userId);
        userDTO.setUserRoleRelations(dto.getUserRoleRelations());
        userDTO.setUserRoles(dto.getUserRoles());
        userDTO.setUserRolePermissions(dto.getList());
        return userDTO;
    }

    private UserRolePermissionDTO getUserRolePermission(String userId) {
        UserRolePermissionDTO permissionDTO = new UserRolePermissionDTO();
        List<UserRoleResourceDTO> list = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).eq(userId).list();
        if (CollectionUtils.isEmpty(userRoleRelations)) {
            return permissionDTO;
        }
        permissionDTO.setUserRoleRelations(userRoleRelations);
        List<String> roleList = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        List<UserRole> userRoles = QueryChain.of(UserRole.class).where(UserRole::getId).in(roleList).list();
        for (UserRole gp : userRoles) {
            UserRoleResourceDTO dto = new UserRoleResourceDTO();
            dto.setUserRole(gp);
            List<UserRolePermission> userRolePermissions = QueryChain.of(UserRolePermission.class)
                    .where(UserRolePermission::getRoleId).eq(gp.getId()).list();
            dto.setUserRolePermissions(userRolePermissions);
            list.add(dto);
        }
        permissionDTO.setList(list);
        return permissionDTO;
    }

    public void autoSwitch(UserDTO userDTO) {

    }
}
