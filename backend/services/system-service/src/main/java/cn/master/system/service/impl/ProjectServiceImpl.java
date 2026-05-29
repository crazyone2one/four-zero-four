package cn.master.system.service.impl;

import cn.master.constants.InternalUserRole;
import cn.master.constants.OperationLogModule;
import cn.master.constants.OperationLogType;
import cn.master.exception.FZFException;
import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.ProjectRequest;
import cn.master.system.dto.project.UpdateProjectNameRequest;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectAddMemberRequest;
import cn.master.system.dto.request.ProjectMemberRequest;
import cn.master.system.dto.request.ProjectSwitchRequest;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.dto.user.UserExtendDTO;
import cn.master.system.dto.user.UserRoleOptionDto;
import cn.master.system.entity.*;
import cn.master.system.mapper.ProjectMapper;
import cn.master.system.mapper.SystemUserMapper;
import cn.master.system.service.CommonProjectService;
import cn.master.system.service.ProjectService;
import cn.master.system.service.SimpleUserService;
import cn.master.util.ServiceUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.master.system.entity.table.OrganizationTableDef.ORGANIZATION;
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

    @Override
    public Page<ProjectDTO> pageProject(ProjectRequest request) {
        Page<ProjectDTO> page = queryChain().select(PROJECT.ALL_COLUMNS)
                .from(PROJECT).innerJoin(ORGANIZATION).on(PROJECT.ORGANIZATION_ID.eq(ORGANIZATION.ID))
                .where(ORGANIZATION.ID.eq(request.getOrganizationId()))
                .and(PROJECT.NAME.like(request.getKeyword()).or(PROJECT.NUM.like(request.getKeyword())))
                .orderBy(PROJECT.CREATE_TIME.desc())
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), ProjectDTO.class);
        return commonProjectService.buildUserInfo(page);
    }

    @Override
    public void delete(String id, String deleteUser) {
        commonProjectService.delete(id, deleteUser);
    }

    @Override
    public void enable(String id, String updateUser) {
        commonProjectService.enable(id, updateUser);
    }

    @Override
    public void disable(String id, String updateUser) {
        commonProjectService.disable(id, updateUser);
    }

    @Override
    public void addMemberByProject(ProjectAddMemberRequest request, String createUser) {
        commonProjectService.addProjectUser(request, createUser, ADD_MEMBER, OperationLogType.ADD.name(), Translator.get("add"), OperationLogModule.SETTING_SYSTEM_ORGANIZATION);
    }

    @Override
    public Page<UserExtendDTO> getProjectMember(ProjectMemberRequest request) {
        QueryChain<UserRoleRelation> userRoleRelationQueryChain = QueryChain.of(UserRoleRelation.class)
                .select(SYSTEM_USER.ALL_COLUMNS, USER_ROLE_RELATION.ROLE_ID, USER_ROLE_RELATION.CREATE_TIME.as("memberTime"))
                .from(USER_ROLE_RELATION).leftJoin(SYSTEM_USER).on(SYSTEM_USER.ID.eq(USER_ROLE_RELATION.USER_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(request.projectId()))
                .and(SYSTEM_USER.NAME.like(request.keyword())
                        .or(SYSTEM_USER.EMAIL.like(request.keyword()))
                        .or(SYSTEM_USER.PHONE.like(request.keyword())))
                .orderBy(USER_ROLE_RELATION.CREATE_TIME.desc());

        Page<UserExtendDTO> page = queryChain().select("temp.*")
                .select("MAX( if (temp.role_id = 'project_admin', true, false)) as adminFlag")
                .select("MIN(temp.memberTime) as groupTime")
                .from(userRoleRelationQueryChain.as("temp")).groupBy("temp.id")
                .orderBy("adminFlag", "groupTime").pageAs(new Page<>(request.page(), request.pageSize()), UserExtendDTO.class);
        List<String> userIds = page.getRecords().stream().map(UserExtendDTO::getId).toList();
        List<UserRoleOptionDto> userRole = selectProjectUserRoleByUserIds(userIds, request.projectId());
        Map<String, List<UserRoleOptionDto>> roleMap = userRole.stream().collect(Collectors.groupingBy(UserRoleOptionDto::getUserId));
        page.getRecords().forEach(user -> {
            if (roleMap.containsKey(user.getId())) {
                user.setUserRoleList(roleMap.get(user.getId()));
            }
        });
        return page;
    }

    @Override
    public int removeProjectMember(String projectId, String userId, String createUser) {
        return commonProjectService.removeProjectMember(projectId, userId, createUser, OperationLogModule.SETTING_SYSTEM_ORGANIZATION, StringUtils.join(REMOVE_PROJECT_MEMBER, projectId, "/", userId));
    }

    @Override
    public void rename(UpdateProjectNameRequest request, String userId) {
        commonProjectService.rename(request, userId);
    }

    @Override
    public Project checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(mapper.selectOneById(id), "permission.project.name");
    }

    private List<UserRoleOptionDto> selectProjectUserRoleByUserIds(List<String> userIds, String projectId) {
        return QueryChain.of(UserRoleRelation.class)
                .select(USER_ROLE_RELATION.USER_ID, USER_ROLE_RELATION.ROLE_ID.as("id"), USER_ROLE.NAME)
                .from(USER_ROLE_RELATION)
                .leftJoin(USER_ROLE).on(USER_ROLE.ID.eq(USER_ROLE_RELATION.ROLE_ID))
                .where(USER_ROLE_RELATION.SOURCE_ID.eq(projectId).and(USER_ROLE_RELATION.USER_ID.in(userIds)))
                .listAs(UserRoleOptionDto.class);
    }

    private void checkOrg(String organizationId) {
        QueryChain.of(Organization.class).where(Organization::getId).eq(organizationId).oneOpt()
                .orElseThrow(() -> new FZFException(Translator.get("organization_not_exist")));
    }
}
