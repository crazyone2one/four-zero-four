package cn.master.system.log.service;

import cn.master.constants.OperationLogModule;
import cn.master.constants.OperationLogType;
import cn.master.constants.TemplateScene;
import cn.master.system.dto.request.CustomFieldUpdateRequest;
import cn.master.system.entity.CustomField;
import cn.master.system.log.dto.LogDTO;
import cn.master.system.service.CustomFieldService;
import cn.master.util.EnumValidator;
import cn.master.util.JsonUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author : 11's papa
 * @since : 2026/5/28, 星期四
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class CustomFieldLogService {
    @Resource
    private CustomFieldService customFieldService;

    public LogDTO addLog(CustomFieldUpdateRequest request) {
        LogDTO dto = new LogDTO(
                null,
                null,
                null,
                null,
                OperationLogType.ADD.name(),
                getOperationLogModule(request.getScene()),
                request.getName());
        dto.setOriginalValue(JsonUtils.toJSONBytes(request));
        return dto;
    }

    public LogDTO updateLog(CustomFieldUpdateRequest request) {
        CustomField customField = customFieldService.getWithCheck(request.getId());
        LogDTO dto = new LogDTO(
                null,
                null,
                customField.getId(),
                null,
                OperationLogType.UPDATE.name(),
                getOperationLogModule(customField.getScene()),
                customField.getName());
        dto.setOriginalValue(JsonUtils.toJSONBytes(customField));
        return dto;
    }

    public LogDTO deleteLog(String id) {
        CustomField customField = customFieldService.getWithCheck(id);
        LogDTO dto = new LogDTO(
                null,
                null,
                customField.getId(),
                null,
                OperationLogType.DELETE.name(),
                getOperationLogModule(customField.getScene()),
                customField.getName());
        dto.setOriginalValue(JsonUtils.toJSONBytes(customField));
        return dto;
    }

    private String getOperationLogModule(String scene) {
        TemplateScene templateScene = EnumValidator.validateEnum(TemplateScene.class, scene);
        assert templateScene != null;
        return switch (templateScene) {
            case API -> OperationLogModule.PROJECT_MANAGEMENT_TEMPLATE_API_FIELD;
            case FUNCTIONAL -> OperationLogModule.PROJECT_MANAGEMENT_TEMPLATE_FUNCTIONAL_FIELD;
            case UI -> OperationLogModule.PROJECT_MANAGEMENT_TEMPLATE_UI_FIELD;
            case BUG -> OperationLogModule.PROJECT_MANAGEMENT_TEMPLATE_BUG_FIELD;
            case TEST_PLAN -> OperationLogModule.PROJECT_MANAGEMENT_TEMPLATE_TEST_PLAN_FIELD;
            case COMMON -> OperationLogModule.COMMON_FIELD;
        };
    }
}
