package cn.master.system.service;

import cn.master.system.dto.project.ProjectDTO;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.dto.request.ProjectSwitchRequest;
import cn.master.system.dto.user.UserDTO;
import com.mybatisflex.core.service.IService;
import cn.master.system.entity.Project;

import java.util.List;

/**
 * 项目 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface ProjectService extends IService<Project> {

    List<Project> getUserProject(String organizationId, String userId);

    ProjectDTO add(UpdateProjectRequest request,String createUser);

    ProjectDTO updateProject(UpdateProjectRequest request, String updateUser);

    UserDTO switchProject(ProjectSwitchRequest request, String userId);
}
