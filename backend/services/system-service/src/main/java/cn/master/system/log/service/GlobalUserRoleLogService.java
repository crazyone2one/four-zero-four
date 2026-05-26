package cn.master.system.log.service;

import cn.master.constants.OperationLogConstants;
import cn.master.constants.OperationLogModule;
import cn.master.constants.OperationLogType;
import cn.master.system.dto.permission.PermissionSettingUpdateRequest;
import cn.master.system.dto.permission.UserRoleUpdateRequest;
import cn.master.system.entity.UserRole;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.service.BaseUserRoleService;
import cn.master.util.JsonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/5/25, 星期一
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class GlobalUserRoleLogService {
    @Resource(name = "baseUserRoleService")
    private BaseUserRoleService baseUserRoleService;

    public LogDTO addLog(UserRoleUpdateRequest request) {
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                null,
                null,
                OperationLogType.ADD.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                request.getName());

        dto.setOriginalValue(JsonUtils.toJSONBytes(request));
        return dto;
    }

    public LogDTO updateLog(UserRoleUpdateRequest request) {
        UserRole userRole = baseUserRoleService.getById(request.getId());
        LogDTO dto = null;
        if (userRole != null) {
            dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    userRole.getId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                    userRole.getName());

            dto.setOriginalValue(JsonUtils.toJSONBytes(userRole));
        }
        return dto;
    }

    public LogDTO updateLog(PermissionSettingUpdateRequest request) {
        UserRole userRole = baseUserRoleService.getById(request.getUserRoleId());
        LogDTO dto = null;
        if (userRole != null) {
            dto = new LogDTO(
                    OperationLogConstants.SYSTEM,
                    OperationLogConstants.SYSTEM,
                    request.getUserRoleId(),
                    null,
                    OperationLogType.UPDATE.name(),
                    OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                    userRole.getName());

            dto.setOriginalValue(JsonUtils.toJSONBytes(request));
        }
        return dto;
    }

    public LogDTO deleteLog(String id) {
        UserRole userRole = baseUserRoleService.getById(id);
        if (userRole == null) {
            return null;
        }
        LogDTO dto = new LogDTO(
                OperationLogConstants.SYSTEM,
                OperationLogConstants.SYSTEM,
                userRole.getId(),
                null,
                OperationLogType.DELETE.name(),
                OperationLogModule.SETTING_SYSTEM_USER_GROUP,
                userRole.getName());

        dto.setOriginalValue(JsonUtils.toJSONBytes(userRole));
        return dto;
    }
}
