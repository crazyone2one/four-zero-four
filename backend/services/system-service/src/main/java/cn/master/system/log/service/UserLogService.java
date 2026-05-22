package cn.master.system.log.service;

import cn.master.constants.HttpMethodConstants;
import cn.master.constants.OperationLogConstants;
import cn.master.constants.OperationLogModule;
import cn.master.constants.OperationLogType;
import cn.master.dto.BatchProcessDTO;
import cn.master.system.dto.user.UserChangeEnableRequest;
import cn.master.system.dto.user.UserCreateInfo;
import cn.master.system.dto.user.UserEditRequest;
import cn.master.system.entity.SystemUser;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.log.dto.LogDTOBuilder;
import cn.master.system.mapper.SystemUserMapper;
import cn.master.system.service.SimpleUserService;
import cn.master.util.JsonUtils;
import cn.master.util.Translator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class UserLogService {
    private final SystemUserMapper userMapper;
    private final SimpleUserService simpleUserService;

    public List<LogDTO> getBatchAddLogs(@Valid List<UserCreateInfo> userList, String operator, String requestPath) {
        List<LogDTO> logs = new ArrayList<>();
        userList.forEach(user -> {
            LogDTO log = LogDTOBuilder.builder()
                    .projectId("SYSTEM")
                    .organizationId("SYSTEM")
                    .type(OperationLogType.ADD.name())
                    .module("SETTING_SYSTEM_USER_SINGLE")
                    .method(HttpMethodConstants.POST.name())
                    .path(requestPath)
                    .sourceId(user.getName())
                    .content(user.getName() + "(" + user.getEmail() + ")")
                    .originalValue(JsonUtils.toJSONBytes(user))
                    .createUser(operator)
                    .build().getLogDTO();
            logs.add(log);
        });
        return logs;
    }

    public LogDTO updateLog(UserEditRequest request) {
        SystemUser user = userMapper.selectOneById(request.getId());
        if (user != null) {
            return LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/update")
                    .sourceId(request.getId())
                    .content(user.getName())
                    .originalValue(JsonUtils.toJSONBytes(user))
                    .build().getLogDTO();
        }
        return null;
    }

    public List<LogDTO> batchUpdateEnableLog(UserChangeEnableRequest request) {
        List<LogDTO> logDTOList = new ArrayList<>();
        request.setSelectIds(simpleUserService.getBatchUserIds(request));
        List<SystemUser> userList = userMapper.selectListByIds(request.getSelectIds());
        for (SystemUser user : userList) {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/update/enable")
                    .sourceId(user.getId())
                    .content((request.isEnable() ? Translator.get("user.enable") : Translator.get("user.disable")) + ":" + user.getName())
                    .originalValue(JsonUtils.toJSONBytes(user))
                    .build().getLogDTO();
            logDTOList.add(dto);
        }
        return logDTOList;
    }

    public List<LogDTO> deleteLog(BatchProcessDTO request) {
        List<LogDTO> logDTOList = new ArrayList<>();
        request.getSelectIds().forEach(item -> {
            SystemUser user = userMapper.selectOneById(item);
            if (user != null) {
                LogDTO dto = LogDTOBuilder.builder()
                        .projectId(OperationLogConstants.SYSTEM)
                        .organizationId(OperationLogConstants.SYSTEM)
                        .type(OperationLogType.DELETE.name())
                        .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                        .method(HttpMethodConstants.POST.name())
                        .path("/system/user/delete")
                        .sourceId(user.getId())
                        .content(user.getName())
                        .originalValue(JsonUtils.toJSONBytes(user))
                        .build().getLogDTO();
                logDTOList.add(dto);

            }
        });
        return logDTOList;
    }

    public List<LogDTO> resetPasswordLog(BatchProcessDTO request) {
        request.setSelectIds(simpleUserService.getBatchUserIds(request));
        List<LogDTO> returnList = new ArrayList<>();
        List<SystemUser> userList = userMapper.selectListByIds(request.getSelectIds());
        for (SystemUser user : userList) {
            LogDTO dto = LogDTOBuilder.builder()
                    .projectId(OperationLogConstants.SYSTEM)
                    .organizationId(OperationLogConstants.SYSTEM)
                    .type(OperationLogType.UPDATE.name())
                    .module(OperationLogModule.SETTING_SYSTEM_USER_SINGLE)
                    .method(HttpMethodConstants.POST.name())
                    .path("/system/user/reset/password")
                    .sourceId(user.getId())
                    .content(Translator.get("user.reset.password") + " : " + user.getName())
                    .originalValue(JsonUtils.toJSONBytes(user))
                    .build().getLogDTO();
            returnList.add(dto);
        }
        return returnList;
    }
}
