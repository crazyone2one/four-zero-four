package cn.master.system.service;

import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.ProjectRequest;
import cn.master.system.dto.project.UpdateProjectNameRequest;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectAddMemberRequest;
import cn.master.system.dto.request.ProjectMemberRequest;
import cn.master.system.dto.request.ProjectSwitchRequest;
import cn.master.system.dto.user.UserDTO;
import cn.master.system.dto.user.UserExtendDTO;
import cn.master.system.entity.Project;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface ProjectService extends IService<Project> {

    List<Project> getUserProject(String organizationId, String userId);

    ProjectDTO add(UpdateProjectRequest request, String createUser);

    ProjectDTO updateProject(UpdateProjectRequest request, String updateUser);

    UserDTO switchProject(ProjectSwitchRequest request, String userId);

    Page<ProjectDTO> pageProject(ProjectRequest request);

    void delete(String id, String deleteUser);

    void enable(String id, String updateUser);

    void disable(String id, String updateUser);

    void addMemberByProject(ProjectAddMemberRequest request, String createUser);

    Page<UserExtendDTO> getProjectMember(ProjectMemberRequest request);

    int removeProjectMember(String projectId, String userId, String createUser);

    void rename(UpdateProjectNameRequest request, String userId);
}
