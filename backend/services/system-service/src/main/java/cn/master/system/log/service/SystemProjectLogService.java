package cn.master.system.log.service;

import cn.master.constants.HttpMethodConstants;
import cn.master.constants.OperationLogConstants;
import cn.master.constants.OperationLogModule;
import cn.master.constants.OperationLogType;
import cn.master.system.dto.project.UpdateProjectNameRequest;
import cn.master.system.dto.project.UpdateProjectRequest;
import cn.master.system.entity.Project;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.mapper.ProjectMapper;
import cn.master.util.JsonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/5/21, 星期四
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class SystemProjectLogService {
    @Resource
    private ProjectMapper projectMapper;
    public LogDTO addLog(UpdateProjectRequest project) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                project.getName());

        dto.setOriginalValue(JsonUtils.toJSONBytes(project));
        return dto;
    }
    public LogDTO updateLog(UpdateProjectRequest request) {
        Project project = projectMapper.selectOneById(request.getId());
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    request.getName());

            dto.setOriginalValue(JsonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
    public LogDTO renameLog(UpdateProjectNameRequest request) {
        Project project = projectMapper.selectOneById(request.id());
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    request.name());

            dto.setOriginalValue(JsonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
    public LogDTO updateLog(String id) {
        Project project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    project.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    project.getName());
            dto.setMethod(HttpMethodConstants.GET.name());

            dto.setOriginalValue(JsonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
    public LogDTO deleteLog(String id) {
        Project project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    id,
                    null,
                    OperationLogType.DELETE.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    project.getName());

            dto.setOriginalValue(JsonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
    public LogDTO recoverLog(String id) {
        Project project = projectMapper.selectOneById(id);
        if (project != null) {
            LogDTO dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    id,
                    null,
                    OperationLogType.RECOVER.name(),
                    OperationLogModule.SETTING_SYSTEM_ORGANIZATION,
                    project.getName());
            dto.setOriginalValue(JsonUtils.toJSONBytes(project));
            return dto;
        }
        return null;
    }
}
