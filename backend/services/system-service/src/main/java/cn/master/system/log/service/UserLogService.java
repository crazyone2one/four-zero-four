package cn.master.system.log.service;

import cn.master.constants.HttpMethodConstants;
import cn.master.constants.OperationLogType;
import cn.master.system.dto.user.UserCreateInfo;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.log.dto.LogDTOBuilder;
import cn.master.util.JsonUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class UserLogService {
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

}
