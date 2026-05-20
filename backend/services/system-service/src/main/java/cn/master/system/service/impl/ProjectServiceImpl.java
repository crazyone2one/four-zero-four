package cn.master.system.service.impl;

import cn.master.constants.InternalUserRole;
import cn.master.constants.OperationLogModule;
import cn.master.exception.FZFException;
import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectSwitchRequest;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.entity.*;
import cn.master.system.mapper.ProjectMapper;
import cn.master.system.mapper.SystemUserMapper;
import cn.master.system.service.CommonProjectService;
import cn.master.system.service.ProjectService;
import cn.master.system.service.SimpleUserService;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static cn.master.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.system.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.system.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;
import static cn.master.system.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 项目 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {
    private final SystemUserMapper userMapper;
    private final CommonProjectService commonProjectService;
    private final SimpleUserService simpleUserService;

    private final static String PREFIX = "/organization-project";
    private final static String ADD_PROJECT = PREFIX + "/add";
    private final static String UPDATE_PROJECT = PREFIX + "/update";
    private final static String REMOVE_PROJECT_MEMBER = PREFIX + "/remove-member/";
    private final static String ADD_MEMBER = PREFIX + "/add-member";

    @Override
    public List<Project> getUserProject(String organizationId, String userId) {
        checkOrg(organizationId);
        SystemUser user = userMapper.selectOneById(userId);
        String projectId;
        if (Objects.nonNull(user) && StringUtils.isNotEmpty(user.getLastProjectId())) {
            projectId = user.getLastProjectId();
        } else {
            projectId = null;
        }
        List<Project> allProject;
        boolean exists = QueryChain.of(UserRoleRelation.class).where(UserRoleRelation::getUserId).eq(userId)
                .and(UserRoleRelation::getRoleId).eq(InternalUserRole.ADMIN.name()).exists();
        if (exists) {
            allProject = queryChain().where(Project::getEnable).eq(true)
                    .and(Project::getOrganizationId).eq(organizationId).list();
        } else {
            allProject = queryChain().select(QueryMethods.distinct(PROJECT.ALL_COLUMNS))
                    .from(UserRole.class)
                    .join(UserRoleRelation.class).on(USER_ROLE_RELATION.ROLE_ID.eq(USER_ROLE.ID))
                    .join(PROJECT).on(PROJECT.ID.eq(USER_ROLE_RELATION.SOURCE_ID))
                    .join(SystemUser.class).on(SYSTEM_USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                    .where(USER_ROLE_RELATION.USER_ID.eq(userId).and(USER_ROLE.TYPE.eq("PROJECT"))
                            .and(PROJECT.ENABLE.eq(true))
                            .and(PROJECT.ORGANIZATION_ID.eq(organizationId)))
                    .orderBy(PROJECT.NAME.asc())
                    .list();
        }
        List<Project> temp = allProject;
        return allProject.stream()
                .filter(project -> Strings.CS.equals(project.getId(), projectId))
                .findFirst()
                .map(project -> {
                    temp.remove(project);
                    temp.addFirst(project);
                    return temp;
                })
                .orElse(allProject);
    }

    @Override
    public ProjectDTO add(UpdateProjectRequest request, String createUser) {
        return commonProjectService.add(request, createUser, ADD_PROJECT, OperationLogModule.SETTING_ORGANIZATION_PROJECT);
    }

    @Override
    public ProjectDTO updateProject(UpdateProjectRequest request, String updateUser) {
        return commonProjectService.update(request, updateUser, UPDATE_PROJECT, OperationLogModule.SETTING_ORGANIZATION_PROJECT);
    }

    @Override
    public UserDTO switchProject(ProjectSwitchRequest request, String currentUserId) {
        if (!Strings.CS.equals(currentUserId, request.userId())) {
            throw new FZFException(Translator.get("not_authorized"));
        }
        if (mapper.selectOneById(request.projectId()) == null) {
            throw new FZFException(Translator.get("project_not_exist"));
        }
        UpdateChain.of(SystemUser.class).set(SYSTEM_USER.LAST_PROJECT_ID, request.projectId())
                .where(SYSTEM_USER.ID.eq(request.userId())).update();
        return simpleUserService.getUser(request.userId());
    }

    private void checkOrg(String organizationId) {
        QueryChain.of(Organization.class).where(Organization::getId).eq(organizationId).oneOpt()
                .orElseThrow(() -> new FZFException(Translator.get("organization_not_exist")));
    }
}
