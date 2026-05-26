package cn.master.system.service.impl;

import cn.master.constants.InternalUserRole;
import cn.master.constants.UserRoleEnum;
import cn.master.constants.UserRoleScope;
import cn.master.constants.UserRoleType;
import cn.master.exception.FZFException;
import cn.master.system.dto.UserSelectOption;
import cn.master.system.dto.permission.Permission;
import cn.master.system.dto.permission.PermissionCache;
import cn.master.system.dto.permission.PermissionDefinitionItem;
import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRolePermission;
import cn.master.system.entity.UserRoleRelation;
import cn.master.system.mapper.UserRoleMapper;
import cn.master.system.service.BaseUserRolePermissionService;
import cn.master.system.service.BaseUserRoleService;
import cn.master.system.service.UserRoleRelationService;
import cn.master.util.JsonUtils;
import cn.master.util.ServiceUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static cn.master.result.SystemResultCode.*;
import static cn.master.system.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service("baseUserRoleService")
public class BaseUserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements BaseUserRoleService {
    protected final BaseUserRolePermissionService baseUserRolePermissionService;
    private final PermissionCache permissionCache;
    protected final UserRoleRelationService userRoleRelationService;

    public BaseUserRoleServiceImpl(@Qualifier("baseUserRolePermissionService")
                                   BaseUserRolePermissionService baseUserRolePermissionService, PermissionCache permissionCache, UserRoleRelationService userRoleRelationService) {
        this.baseUserRolePermissionService = baseUserRolePermissionService;
        this.permissionCache = permissionCache;
        this.userRoleRelationService = userRoleRelationService;
    }

    private final List<String> firstLevelIgnorePermissionIds = Arrays.asList("ORGANIZATION");
    private final List<String> secondLevelIgnorePermissionIds = Arrays.asList("SYSTEM_TEST_RESOURCE_POOL", "SYSTEM_PLUGIN", "SYSTEM_AUTHORIZATION_MANAGEMENT");
    private final List<String> ignorePermissionIds = Arrays.asList("SYSTEM_PARAMETER_SETTING_BASE:READ", "SYSTEM_PARAMETER_SETTING_BASE:READ+UPDATE");

    @Override
    public void checkRoleIsGlobalAndHaveMember(List<String> roleIdList, boolean isSystem) {
        QueryChain<UserRole> queryChain = queryChain().where(USER_ROLE.ID.in(roleIdList));
        if (isSystem) {
            queryChain.and(USER_ROLE.TYPE.eq("SYSTEM"));
        } else {
            queryChain.and(USER_ROLE.TYPE.eq("GLOBAL"));
        }
        if (queryChain.count() != roleIdList.size()) {
            throw new FZFException(Translator.get("role.not.global"));
        }
    }

    @Override
    public List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations) {
        List<String> roleIds = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        return mapper.selectListByIds(roleIds);
    }

    @Override
    public List<UserSelectOption> getGlobalSystemRoleList() {
        List<UserSelectOption> returnList = new ArrayList<>();
        List<UserRole> userRoles = queryChain().where(USER_ROLE.SCOPE_ID.eq(UserRoleScope.GLOBAL)
                .and(USER_ROLE.TYPE.eq(UserRoleType.SYSTEM.name()))).list();
        userRoles.forEach(item ->
                returnList.add(new UserSelectOption(item.getId(),
                        item.getName(),
                        Strings.CS.equals(item.getId(), InternalUserRole.MEMBER.getValue()),
                        !Strings.CS.equals(item.getId(), InternalUserRole.MEMBER.getValue()))));
        return returnList;
    }

    @Override
    public UserRole checkResourceExist(UserRole userRole) {
        return ServiceUtils.checkResourceExist(userRole, "permission.system_user_role.name");
    }

    @Override
    public UserRole getWithCheck(String id) {
        return checkResourceExist(mapper.selectOneById(id));
    }

    @Override
    public void checkGlobalUserRole(UserRole userRole) {
        if (UserRoleEnum.GLOBAL.toString().equals(userRole.getScopeId())) {
            throw new FZFException(NO_GLOBAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public List<PermissionDefinitionItem> getPermissionSetting(UserRole userRole) {
        // 获取该用户组拥有的权限
        Set<String> permissionIds = baseUserRolePermissionService.getPermissionIdSetByRoleId(userRole.getId());
        List<PermissionDefinitionItem> permissionDefinition = permissionCache.getPermissionDefinition();
        permissionDefinition = JsonUtils.parseArray(JsonUtils.toJSONString(permissionDefinition), PermissionDefinitionItem.class);
        permissionDefinition = permissionDefinition.stream()
                .filter(item -> item.getType().equals(userRole.getType()) || InternalUserRole.ADMIN.getValue().equals(userRole.getId()))
                .sorted(Comparator.comparing(PermissionDefinitionItem::getOrder))
                .collect(Collectors.toList());
        permissionDefinition.removeIf(item -> firstLevelIgnorePermissionIds.contains(item.getId()));
        for (PermissionDefinitionItem firstLevel : permissionDefinition) {
            List<PermissionDefinitionItem> children = firstLevel.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                children.removeIf(item -> secondLevelIgnorePermissionIds.contains(item.getId()));
            }
            boolean allCheck = true;
            firstLevel.setName(Translator.get(firstLevel.getName()));
            for (PermissionDefinitionItem secondLevel : children) {
                List<Permission> permissions = secondLevel.getPermissions();
                permissions.removeIf(item -> secondLevel.getId().equals("SYSTEM_PARAMETER_SETTING") && !ignorePermissionIds.contains(item.getId()));
                secondLevel.setName(Translator.get(secondLevel.getName()));
                if (CollectionUtils.isEmpty(permissions)) {
                    continue;
                }
                boolean secondAllCheck = true;
                for (Permission p : permissions) {
                    if (StringUtils.isNotBlank(p.getName())) {
                        // 有 name 字段翻译 name 字段
                        p.setName(Translator.get(p.getName()));
                    } else {
                        p.setName(translateDefaultPermissionName(p));
                    }
                    // 管理员默认勾选全部二级权限位
                    if (permissionIds.contains(p.getId()) || InternalUserRole.ADMIN.getValue().equals(userRole.getId())) {
                        p.setEnable(true);
                    } else {
                        // 如果权限有未勾选，则二级菜单设置为未勾选
                        p.setEnable(false);
                        secondAllCheck = false;
                    }
                }
                secondLevel.setEnable(secondAllCheck);
                if (!secondAllCheck) {
                    // 如果二级菜单有未勾选，则一级菜单设置为未勾选
                    allCheck = false;
                }
            }
            firstLevel.setEnable(allCheck);
        }
        return permissionDefinition;
    }

    @Override
    public void checkAdminUserRole(UserRole userRole) {
        if (Strings.CS.equalsAny(userRole.getId(), InternalUserRole.ADMIN.getValue(),
                InternalUserRole.ORG_ADMIN.getValue(), InternalUserRole.PROJECT_ADMIN.getValue())) {
            throw new FZFException(ADMIN_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public void updatePermissionSetting(PermissionSettingUpdateRequest request) {
        baseUserRolePermissionService.updatePermissionSetting(request);
    }

    @Override
    public void checkInternalUserRole(UserRole userRole) {
        if (BooleanUtils.isTrue(userRole.getInternal())) {
            throw new FZFException(INTERNAL_USER_ROLE_PERMISSION);
        }
    }

    @Override
    public UserRole add(UserRole userRole) {
        mapper.insertSelective(userRole);
        if (UserRoleType.PROJECT.name().equals(userRole.getType())) {
            // 项目级别用户组, 初始化基本信息权限
            UserRolePermission initPermission = new UserRolePermission();
            initPermission.setRoleId(userRole.getId());
            initPermission.setPermissionId("PROJECT_BASE_INFO:READ");
            baseUserRolePermissionService.save(initPermission);
        }
        return userRole;
    }

    @Override
    public UserRole update(UserRole userRole) {
        mapper.update(userRole);
        return userRole;
    }

    @Override
    public void delete(UserRole userRole, String defaultRoleId, String currentUserId, String orgId) {
        String id = userRole.getId();
        checkInternalUserRole(userRole);
        // 删除用户组的权限设置
        baseUserRolePermissionService.remove(QueryChain.of(UserRolePermission.class).where(UserRolePermission::getRoleId).eq(id));
        // 删除用户组
        mapper.deleteById(id);
        // 检查是否只有一个用户组，如果是则添加系统成员等默认用户组
        checkOneLimitRole(id, defaultRoleId, currentUserId, orgId);
        // 删除用户组与用户的关联关系
        userRoleRelationService.deleteByRoleId(id);
    }

    private void checkOneLimitRole(String roleId, String defaultRoleId, String currentUserId, String orgId) {
        List<String> userIds = userRoleRelationService.getUserIdByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        Map<String, List<UserRoleRelation>> userRoleRelationMap = userRoleRelationService
                .getUserIdAndSourceIdByUserIds(userIds)
                .stream()
                .collect(Collectors.groupingBy(i -> i.getUserId() + i.getSourceId()));
        List<UserRoleRelation> addRelations = new ArrayList<>();
        userRoleRelationMap.forEach((groupId, relations) -> {
            // 如果当前用户组只有一个用户，并且就是要删除的用户组，则添加组织成员等默认用户组
            if (relations.size() == 1 && Strings.CS.equals(relations.getFirst().getRoleId(), roleId)) {
                UserRoleRelation relation = new UserRoleRelation();
                relation.setUserId(relations.getFirst().getUserId());
                relation.setSourceId(relations.getFirst().getSourceId());
                relation.setRoleId(defaultRoleId);
                relation.setCreateUser(currentUserId);
                relation.setOrganizationId(orgId);
                addRelations.add(relation);
            }
        });
        userRoleRelationService.saveBatch(addRelations);
    }

    private String translateDefaultPermissionName(Permission p) {
        if (StringUtils.isNotBlank(p.getName())) {
            p.getName();
        }
        String[] idSplit = p.getId().split(":");
        String permissionKey = idSplit[idSplit.length - 1];
        Map<String, String> translationMap = new HashMap<>() {{
            put("READ", "permission.read");
            put("READ+ADD", "permission.add");
            put("READ+UPDATE", "permission.edit");
            put("READ+DELETE", "permission.delete");
            put("READ+IMPORT", "permission.import");
            put("READ+RECOVER", "permission.recover");
            put("READ+EXPORT", "permission.export");
            put("READ+EXECUTE", "permission.execute");
            put("READ+DEBUG", "permission.debug");
        }};
        return Translator.get(translationMap.get(permissionKey));
    }
}
