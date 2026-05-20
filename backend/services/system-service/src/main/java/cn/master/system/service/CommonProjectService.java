package cn.master.system.service;

import cn.master.constants.*;
import cn.master.exception.FZFException;
import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectAddMemberBatchRequest;
import cn.master.system.entity.Project;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRoleRelation;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.mapper.ProjectMapper;
import cn.master.system.mapper.SystemUserMapper;
import cn.master.system.mapper.UserRoleRelationMapper;
import cn.master.util.JsonUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.system.entity.table.ProjectTableDef.PROJECT;
import static cn.master.system.entity.table.SystemUserTableDef.SYSTEM_USER;
import static cn.master.system.entity.table.UserRoleRelationTableDef.USER_ROLE_RELATION;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class CommonProjectService {
    private final ProjectMapper projectMapper;
    private final UserRoleRelationMapper userRoleRelationMapper;
    private final OperationLogService operationLogService;
    private final SystemUserMapper userMapper;

    public ProjectDTO add(UpdateProjectRequest request, String createUser, String path, String module) {
        Project project = new Project();
        project.setName(request.getName());
        project.setNum(request.getNum());
        project.setOrganizationId(request.getOrganizationId());
        checkProjectExistByName(project);
        project.setUpdateUser(createUser);
        project.setCreateUser(createUser);
        project.setEnable(request.getEnable());
        project.setDescription(request.getDescription());
        projectMapper.insertSelective(project);
        ProjectDTO projectDTO = new ProjectDTO();
        BeanUtils.copyProperties(project, projectDTO);
        ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
        memberRequest.setProjectIds(List.of(project.getId()));
        memberRequest.setUserIds(request.getUserIds());
        addProjectAdmin(memberRequest, createUser, path, OperationLogType.ADD.name(), Translator.get("add"), module);
        return projectDTO;
    }

    public ProjectDTO update(UpdateProjectRequest updateProjectDto, String updateUser, String path, String module) {
        Project project = new Project();
        ProjectDTO projectDTO = new ProjectDTO();
        project.setId(updateProjectDto.getId());
        project.setName(updateProjectDto.getName());
        project.setNum(updateProjectDto.getNum());
        project.setDescription(updateProjectDto.getDescription());
        project.setOrganizationId(updateProjectDto.getOrganizationId());
        project.setEnable(updateProjectDto.getEnable());
        project.setUpdateUser(updateUser);
        checkProjectExistByName(project);
        checkProjectNotExist(project.getId());
        BeanUtils.copyProperties(project, projectDTO);
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))).list();
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        List<LogDTO> logDTOList = new ArrayList<>();
        List<String> deleteIds = orgUserIds.stream()
                .filter(item -> !updateProjectDto.getUserIds().contains(item))
                .toList();
        List<String> insertIds = updateProjectDto.getUserIds().stream()
                .filter(item -> !orgUserIds.contains(item))
                .toList();
        if (CollectionUtils.isNotEmpty(deleteIds)) {
            QueryChain<UserRoleRelation> queryChain = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(project.getId())
                    .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue()))
                    .and(USER_ROLE_RELATION.USER_ID.in(deleteIds)));
            queryChain.list().forEach(userRoleRelation -> {
                SystemUser user = userMapper.selectOneById(userRoleRelation.getUserId());
                String logProjectId = OperationLogConstants.SYSTEM;
                if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                    logProjectId = OperationLogConstants.ORGANIZATION;
                }
                LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), userRoleRelation.getId(), updateUser, OperationLogType.DELETE.name(), module, Translator.get("delete") + Translator.get("project_admin") + ": " + user.getName());
                setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
            });
            userRoleRelationMapper.deleteByQuery(queryChain);
        }
        if (CollectionUtils.isNotEmpty(insertIds)) {
            ProjectAddMemberBatchRequest memberRequest = new ProjectAddMemberBatchRequest();
            memberRequest.setProjectIds(List.of(project.getId()));
            memberRequest.setUserIds(insertIds);
            addProjectAdmin(memberRequest, updateUser, path, OperationLogType.ADD.name(), Translator.get("add"), module);
        }
        if (CollectionUtils.isNotEmpty(logDTOList)) {
            operationLogService.batchAdd(logDTOList);
        }
        projectMapper.update(project);
        return projectDTO;
    }

    private void addProjectAdmin(ProjectAddMemberBatchRequest request, String createUser, String path, String type, String content, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = new ArrayList<>();
        request.getProjectIds().forEach(projectId -> {
            Project project = projectMapper.selectOneById(projectId);
            Map<String, String> nameMap = addUserPre(request.getUserIds(), createUser, path, module, projectId, project);
            request.getUserIds().forEach(userId -> {
                boolean exists = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.SOURCE_ID.eq(projectId)
                                .and(USER_ROLE_RELATION.ROLE_ID.eq(InternalUserRole.PROJECT_ADMIN.getValue())))
                        .exists();
                if (!exists) {
                    UserRoleRelation adminRole = new UserRoleRelation();
                    adminRole.setUserId(userId);
                    adminRole.setRoleId(InternalUserRole.PROJECT_ADMIN.getValue());
                    adminRole.setSourceId(projectId);
                    adminRole.setCreateUser(createUser);
                    assert project != null;
                    adminRole.setOrganizationId(project.getOrganizationId());
                    userRoleRelations.add(adminRole);
                    String logProjectId = OperationLogConstants.SYSTEM;
                    if (Strings.CS.equals(module, OperationLogModule.SETTING_ORGANIZATION_PROJECT)) {
                        logProjectId = OperationLogConstants.ORGANIZATION;
                    }
                    LogDTO logDTO = new LogDTO(logProjectId, project.getOrganizationId(), adminRole.getId(), createUser, type, module, content + Translator.get("project_admin") + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
        });
        if (CollectionUtils.isNotEmpty(userRoleRelations)) {
            userRoleRelationMapper.insertBatch(userRoleRelations);
        }
        operationLogService.batchAdd(logDTOList);
    }

    private Map<String, String> addUserPre(List<String> userIds, String createUser, String path, String module, String projectId, Project project) {
        checkProjectNotExist(projectId);
        List<SystemUser> users = QueryChain.of(SystemUser.class).where(SYSTEM_USER.ID.in(userIds)).list();
        if (users.size() != userIds.size()) {
            throw new FZFException(Translator.get("user_not_exist"));
        }
        // 把id和名称放一个map中
        Map<String, String> userMap = users.stream().collect(Collectors.toMap(SystemUser::getId, SystemUser::getName));
        checkOrgRoleExit(userIds, project.getOrganizationId(), createUser, userMap, path, module);
        return userMap;
    }

    private void checkOrgRoleExit(List<String> userIds, String organizationId, String createUser, Map<String, String> nameMap, String path, String module) {
        List<LogDTO> logDTOList = new ArrayList<>();
        List<UserRoleRelation> userRoleRelations = QueryChain.of(UserRoleRelation.class).where(USER_ROLE_RELATION.USER_ID.in(userIds)
                .and(USER_ROLE_RELATION.SOURCE_ID.eq(organizationId))).list();
        List<String> orgUserIds = userRoleRelations.stream().map(UserRoleRelation::getUserId).toList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<UserRoleRelation> userRoleRelation = new ArrayList<>();
            userIds.forEach(userId -> {
                if (!orgUserIds.contains(userId)) {
                    UserRoleRelation memberRole = new UserRoleRelation();
                    memberRole.setUserId(userId);
                    memberRole.setRoleId(InternalUserRole.ORG_MEMBER.getValue());
                    memberRole.setSourceId(organizationId);
                    memberRole.setCreateUser(createUser);
                    memberRole.setOrganizationId(organizationId);
                    userRoleRelation.add(memberRole);
                    LogDTO logDTO = new LogDTO(organizationId, organizationId, memberRole.getId(), createUser, OperationLogType.ADD.name(), module, Translator.get("add") + Translator.get("organization_member") + ": " + nameMap.get(userId));
                    setLog(logDTO, path, HttpMethodConstants.POST.name(), logDTOList);
                }
            });
            if (CollectionUtils.isNotEmpty(userRoleRelation)) {
                userRoleRelationMapper.insertBatch(userRoleRelation);
            }
        }
        operationLogService.batchAdd(logDTOList);
    }

    private void checkProjectNotExist(String projectId) {
        if (projectMapper.selectOneById(projectId) == null) {
            throw new FZFException(Translator.get("project_is_not_exist"));
        }
    }

    public void setLog(LogDTO dto, String path, String method, List<LogDTO> logDTOList) {
        dto.setPath(path);
        dto.setMethod(method);
        dto.setOriginalValue(JsonUtils.toJSONBytes(StringUtils.EMPTY));
        logDTOList.add(dto);
    }

    private void checkProjectExistByName(Project project) {
        boolean exists = QueryChain.of(Project.class).where(PROJECT.NAME.eq(project.getName())
                .and(PROJECT.ORGANIZATION_ID.eq(project.getOrganizationId()))
                .and(PROJECT.NUM.eq(project.getNum()))
                .and(PROJECT.ID.ne(project.getId()))).exists();
        if (exists) {
            throw new FZFException(Translator.get("project_name_already_exists"));
        }
    }
}
