package cn.master.system.service;

import cn.master.system.dto.request.ProjectParamsRequest;
import cn.master.system.entity.ProjectParameter;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 项目级参数 服务层。
 *
 * @author 11's papa
 * @since 2026-06-10
 */
public interface ProjectParameterService extends IService<ProjectParameter> {

    ProjectParameter add(ProjectParamsRequest request, String userId);

    ProjectParameter updateParam(ProjectParamsRequest request, String userId);

    List<ProjectParameter> listByProjectId(String projectId);
}
