package cn.master.system.service.impl;

import cn.master.constants.*;
import cn.master.exception.FZFException;
import cn.master.result.SystemResultCode;
import cn.master.system.dto.user.UserTableVO;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRoleRelation;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.mapper.UserRoleRelationMapper;
import cn.master.system.service.OperationLogService;
import cn.master.system.service.UserRoleRelationService;
import cn.master.util.JsonUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.result.SystemResultCode.USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION;
import static cn.master.system.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.system.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户组关系 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
@RequiredArgsConstructor
public class UserRoleRelationServiceImpl extends ServiceImpl<UserRoleRelationMapper, UserRoleRelation> implements UserRoleRelationService {
    private final OperationLogService operationLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserRoleRelation(List<String> userIds, List<String> roleIds, String operator) {
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(roleIds)) {
            userIds.forEach(userId -> {
                roleIds.forEach(roleId -> {
                    UserRoleRelation userRoleRelation = new UserRoleRelation();
                    userRoleRelation.setUserId(userId);
                    userRoleRelation.setRoleId(roleId);
                    userRoleRelation.setSourceId("system");
                    checkExist(userRoleRelation);
                    userRoleRelation.setOrganizationId("system");
                    userRoleRelation.setCreateUser(operator);
                    userRoleRelations.add(userRoleRelation);
                });
            });
        }
        mapper.insertBatch(userRoleRelations);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserIdList(List<String> userIdList) {
        QueryWrapper wrapper = QueryWrapper.create().where(USER_ROLE_RELATION.USER_ID.in(userIdList));
        mapper.deleteByQuery(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserSystemGlobalRole(SystemUser user, String operator, List<String> roleList) {
        List<String> deleteRoleList = new ArrayList<>();
        List<UserRoleRelation> saveList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelationList = selectGlobalRoleByUserId(user.getId());
        List<String> userSavedRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).toList();
        // 获取要移除的权限
        for (String userSavedRoleId : userSavedRoleIdList) {
            if (!roleList.contains(userSavedRoleId)) {
                deleteRoleList.add(userSavedRoleId);
            }
        }
        // 获取要添加的权限
        for (String roleId : roleList) {
            if (!userSavedRoleIdList.contains(roleId)) {
                UserRoleRelation userRoleRelation = new UserRoleRelation();
                userRoleRelation.setUserId(user.getId());
                userRoleRelation.setRoleId(roleId);
                userRoleRelation.setSourceId(UserRoleScope.SYSTEM);
                userRoleRelation.setCreateUser(operator);
                userRoleRelation.setOrganizationId(UserRoleScope.SYSTEM);
                saveList.add(userRoleRelation);
            }
        }

        if (CollectionUtils.isNotEmpty(deleteRoleList)) {
            List<String> deleteIdList = new ArrayList<>();
            userRoleRelationList.forEach(item -> {
                if (deleteRoleList.contains(item.getRoleId())) {
                    deleteIdList.add(item.getId());
                }
            });
            mapper.deleteBatchByIds(deleteIdList);
            // 记录删除日志
            operationLogService.batchAdd(getBatchLogs(deleteRoleList, user, "updateUser", operator, OperationLogType.DELETE.name()));
        }
        if (CollectionUtils.isNotEmpty(saveList)) {
            // 系统级权限不会太多，所以暂时不分批处理
            saveList.forEach(item -> mapper.insert(item));
            // 记录添加日志
            operationLogService.batchAdd(getBatchLogs(saveList.stream().map(UserRoleRelation::getRoleId).toList(),
                    user, "updateUser", operator, OperationLogType.ADD.name()));
        }
    }

    @Override
    public Map<String, UserTableVO> selectGlobalUserRoleAndOrganization(List<String> userIdList) {
        List<UserRoleRelation> userRoleRelationList = queryChain().where(USER_ROLE_RELATION.USER_ID.in(userIdList)).list();
        List<String> userRoleIdList = userRoleRelationList.stream().map(UserRoleRelation::getRoleId).distinct().toList();
        Map<String, UserRole> userRoleMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userRoleIdList)) {
            userRoleMap = QueryChain.of(UserRole.class).where(USER_ROLE.ID.in(userRoleIdList)
                            .and(USER_ROLE.SCOPE_ID.eq(UserRoleEnum.GLOBAL.toString()))).list().stream()
                    .collect(Collectors.toMap(UserRole::getId, item -> item));
        }
        Map<String, UserTableVO> returnMap = new HashMap<>();
        for (UserRoleRelation userRoleRelation : userRoleRelationList) {
            UserTableVO userInfo = returnMap.get(userRoleRelation.getUserId());
            if (userInfo == null) {
                userInfo = new UserTableVO();
                userInfo.setId(userRoleRelation.getUserId());
                returnMap.put(userRoleRelation.getUserId(), userInfo);
            }
            UserRole userRole = userRoleMap.get(userRoleRelation.getRoleId());
            if (userRole != null && Strings.CI.equals(userRole.getType(), UserRoleScope.SYSTEM)) {
                userInfo.setUserRole(userRole);
            }
        }
        return returnMap;
    }

    @Override
    public List<UserRoleRelation> selectByUserId(String userId) {
        return queryChain().where(USER_ROLE_RELATION.USER_ID.eq(userId)).list();
    }

    @Override
    public void deleteByRoleId(String roleId) {
        QueryChain<UserRoleRelation> chain = queryChain().where(USER_ROLE_RELATION.ROLE_ID.eq(roleId));
        List<UserRoleRelation> userRoleRelations = chain.list();
        userRoleRelations.forEach(userRoleRelation ->
                checkAdminPermissionRemove(userRoleRelation.getUserId(), userRoleRelation.getRoleId()));
        mapper.deleteByQuery(chain);
    }

    @Override
    public List<String> getUserIdByRoleId(String roleId) {
        return queryChain().select(USER_ROLE_RELATION.USER_ID).where(USER_ROLE_RELATION.ROLE_ID.eq(roleId)).listAs(String.class);
    }

    @Override
    public List<UserRoleRelation> getUserIdAndSourceIdByUserIds(List<String> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return List.of();
        }
        return queryChain().where(USER_ROLE_RELATION.USER_ID.in(userIds)).list();
    }

    private void checkAdminPermissionRemove(String userId, String roleId) {
        if (InternalUserRole.ADMIN.getValue().equals(roleId) && Strings.CS.equals(userId, InternalUserRole.ADMIN.getValue())) {
            throw new FZFException(USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION);
        }
    }

    private List<UserRoleRelation> selectGlobalRoleByUserId(String userId) {
        List<String> roleIds = QueryChain.of(UserRole.class).select(USER_ROLE.ID).from(USER_ROLE)
                .where(USER_ROLE.TYPE.eq("SYSTEM").and(USER_ROLE.SCOPE_ID.eq("global")))
                .listAs(String.class);
        return queryChain()
                .where(USER_ROLE_RELATION.USER_ID.eq(userId).and(USER_ROLE_RELATION.ROLE_ID.in(roleIds))).list();
    }

    private List<LogDTO> getBatchLogs(List<String> userRoleId, SystemUser user, String operationMethod, String operator, String operationType) {
        List<LogDTO> logs = new ArrayList<>();
        List<UserRole> userRoleList = QueryChain.of(UserRole.class).where(USER_ROLE.ID.in(userRoleId)).list();
        userRoleList.forEach(userRole -> {
            LogDTO log = new LogDTO();
            log.setProjectId(OperationLogConstants.SYSTEM);
            log.setOrganizationId(OperationLogConstants.SYSTEM);
            log.setType(operationType);
            log.setCreateUser(operator);
            log.setModule("SETTING_SYSTEM_USER_SINGLE");
            log.setMethod(operationMethod);
            log.setSourceId(user.getId());
            log.setContent(user.getName() + StringUtils.SPACE
                    + Translator.get(StringUtils.lowerCase(operationType)) + StringUtils.SPACE
                    + Translator.get("permission.project_group.name") + StringUtils.SPACE
                    + userRole.getName());
            log.setOriginalValue(JsonUtils.toJSONBytes(userRole));
            logs.add(log);
        });
        return logs;
    }

    private void checkExist(UserRoleRelation userRoleRelation) {
        if (queryChain().where(USER_ROLE_RELATION.USER_ID.eq(userRoleRelation.getUserId())
                .and(USER_ROLE_RELATION.ROLE_ID.eq(userRoleRelation.getRoleId()))).exists()) {
            throw new FZFException(SystemResultCode.USER_ROLE_RELATION_EXIST);
        }
    }
}
