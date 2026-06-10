package cn.master.system.service.impl;

import cn.master.exception.FZFException;
import cn.master.system.dto.request.ProjectParamsRequest;
import cn.master.system.entity.Project;
import cn.master.system.entity.ProjectParameter;
import cn.master.system.mapper.ProjectParameterMapper;
import cn.master.system.service.ProjectParameterService;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 项目级参数 服务层实现。
 *
 * @author 11's papa
 * @since 2026-06-10
 */
@Service
public class ProjectParameterServiceImpl extends ServiceImpl<ProjectParameterMapper, ProjectParameter> implements ProjectParameterService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectParameter add(ProjectParamsRequest request, String userId) {
        checkParam(request);
        checkProjectExist(request.projectId());
        ProjectParameter projectParameter = new ProjectParameter();
        projectParameter.setProjectId(request.projectId());
        projectParameter.setParamType(request.paramType());
        projectParameter.setCreateUser(userId);
        projectParameter.setUpdateUser(userId);
        projectParameter.setParameters(request.parameters());
        mapper.insert(projectParameter);
        return projectParameter;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectParameter updateParam(ProjectParamsRequest request, String userId) {
        checkProjectExist(request.projectId());
        checkDataExist(request);
        ProjectParameter projectParameter = new ProjectParameter();
        projectParameter.setId(request.id());
        projectParameter.setUpdateUser(userId);
        projectParameter.setParameters(request.parameters());
        mapper.update(projectParameter);
        return projectParameter;
    }

    @Override
    public List<ProjectParameter> listByProjectId(String projectId) {
        return queryChain().where(ProjectParameter::getProjectId).eq(projectId).list();
    }

    private void checkDataExist(ProjectParamsRequest request) {
        boolean exists = queryChain().where(ProjectParameter::getProjectId).eq(request.projectId())
                .and(ProjectParameter::getParamType).eq(request.paramType()).exists();
        if (!exists) {
            throw new FZFException(Translator.get("global_parameters_is_not_exist"));
        }
    }

    private void checkParam(ProjectParamsRequest request) {
        boolean exists = queryChain().where(ProjectParameter::getProjectId).eq(request.projectId())
                .and(ProjectParameter::getParamType).eq(request.paramType()).exists();
        if (exists) {
            throw new FZFException(Translator.get("global_parameters_already_exist"));
        }
    }

    private void checkProjectExist(String projectId) {
        Project project = QueryChain.of(Project.class).where(Project::getId).eq(projectId).one();
        if (Objects.isNull(project)) {
            throw new FZFException(Translator.get("project_is_not_exist"));
        }
    }
}
