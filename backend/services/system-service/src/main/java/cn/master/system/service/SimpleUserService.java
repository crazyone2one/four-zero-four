package cn.master.system.service;

import cn.master.dto.BatchProcessDTO;
import cn.master.system.dto.OptionDTO;
import cn.master.system.dto.request.PersonalLocaleRequest;
import cn.master.system.dto.user.UserBaseVO;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.dto.user.UserRolePermissionDTO;
import cn.master.system.dto.user.UserRoleResourceDTO;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRolePermission;
import cn.master.system.entity.UserRoleRelation;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.system.entity.table.SystemUserTableDef.SYSTEM_USER;

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

    public UserBaseVO getUserBase(String userId) {
        return QueryChain.of(SystemUser.class).where(SystemUser::getId).eq(userId).oneAs(UserBaseVO.class);
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

    public void updateLanguage(PersonalLocaleRequest request, String operator) {
        UpdateChain.of(SystemUser.class)
                .set(SystemUser::getLanguage, request.language())
                .where(SystemUser::getId).eq(operator)
                .update();
    }

    public List<String> getBatchUserIds(BatchProcessDTO request) {
        if (request.isSelectAll()) {
            List<String> userIdList = new ArrayList<>(QueryChain.of(SystemUser.class).where(SYSTEM_USER.EMAIL.like(request.getCondition().getKeyword())
                            .or(SYSTEM_USER.NAME.like(request.getCondition().getKeyword()))
                            .or(SYSTEM_USER.PHONE.like(request.getCondition().getKeyword()))).list()
                    .stream().map(SystemUser::getId).toList());
            if (CollectionUtils.isNotEmpty(request.getExcludeIds())) {
                userIdList.removeAll(request.getExcludeIds());
            }
            return userIdList;
        } else {
            return request.getSelectIds();
        }
    }

    public List<OptionDTO> getSelectOptionByIdsWithDeleted(List<String> ids) {
        return QueryChain.of(SystemUser.class).select(SYSTEM_USER.ID, SYSTEM_USER.NAME)
                .where(SYSTEM_USER.ID.in(ids))
                .listAs(OptionDTO.class);
    }

    public Map<String, String> getUserNameMap(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return getSelectOptionByIdsWithDeleted(ids)
                .stream()
                .collect(Collectors.toMap(OptionDTO::getId, OptionDTO::getName));
    }

    public List<SystemUser> getUserList(String keyword) {
        return QueryChain.of(SystemUser.class)
                .select(QueryMethods.distinct(SYSTEM_USER.ID, SYSTEM_USER.NAME, SYSTEM_USER.EMAIL))
                .select(SYSTEM_USER.CREATE_TIME)
                .where(SYSTEM_USER.NAME.like(keyword).or(SYSTEM_USER.EMAIL.like(keyword)))
                .orderBy(SYSTEM_USER.CREATE_TIME.desc()).limit(1000).list();
    }
}
