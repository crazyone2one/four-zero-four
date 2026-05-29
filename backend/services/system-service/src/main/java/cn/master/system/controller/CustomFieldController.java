package cn.master.system.controller;

import cn.master.constants.OperationLogType;
import cn.master.security.util.SecurityUtils;
import cn.master.system.dto.CustomFieldDTO;
import cn.master.system.dto.request.CustomFieldPageRequest;
import cn.master.system.dto.request.CustomFieldUpdateRequest;
import cn.master.system.entity.CustomField;
import cn.master.system.log.annotation.Log;
import cn.master.system.log.service.CustomFieldLogService;
import cn.master.system.service.CustomFieldService;
import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 自定义字段 控制层。
 *
 * @author 11's papa
 * @since 2026-05-26
 */
@RestController
@Tag(name = "自定义字段接口")
@RequiredArgsConstructor
@RequestMapping("/custom/field")
public class CustomFieldController {

    private final CustomFieldService customFieldService;

    @PostMapping("save")
    @Operation(description = "保存自定义字段")
    @Log(type = OperationLogType.ADD, expression = "#clazz.addLog(#request)", clazz = CustomFieldLogService.class)
    public CustomField save(@RequestBody @Parameter(description = "自定义字段") @Validated({Created.class}) CustomFieldUpdateRequest request) {
        CustomField customField = new CustomField();
        BeanUtils.copyProperties(request, customField);
        customField.setCreateUser(SecurityUtils.getUserId());
        return customFieldService.add(customField, request.getOptions());
    }

    @GetMapping("remove/{id}")
    @Operation(description = "删除自定义字段")
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.deleteLog(#request)", clazz = CustomFieldLogService.class)
    public void remove(@PathVariable @Parameter(description = "自定义字段主键") String id) {
        customFieldService.delete(id);
    }

    @PostMapping("update")
    @Operation(description = "更新自定义字段")
    @Log(type = OperationLogType.UPDATE, expression = "#clazz.updateLog(#request)", clazz = CustomFieldLogService.class)
    public CustomField update(@RequestBody @Parameter(description = "自定义字段主键") @Validated({Updated.class}) CustomFieldUpdateRequest request) {
        CustomField customField = new CustomField();
        BeanUtils.copyProperties(request, customField);
        return customFieldService.update(customField, request.getOptions());
    }

    /**
     * 查询所有自定义字段。
     *
     * @return 所有数据
     */
    @GetMapping("/list/{scopeId}/{scene}")
    @Operation(description = "查询所有自定义字段")
    public List<CustomFieldDTO> list(@Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
                                  @PathVariable String scopeId,
                                  @Schema(description = "模板的使用场景（FUNCTIONAL,BUG,API,UI,TEST_PLAN）", requiredMode = Schema.RequiredMode.REQUIRED)
                                  @PathVariable String scene) {
        return customFieldService.listCustomField(scopeId, scene);
    }

    /**
     * 根据主键获取自定义字段。
     *
     * @param id 自定义字段主键
     * @return 自定义字段详情
     */
    @GetMapping("getInfo/{id}")
    @Operation(description = "根据主键获取自定义字段")
    public CustomFieldDTO getInfo(@PathVariable @Parameter(description = "自定义字段主键") String id) {
        return customFieldService.getCustomFieldDTOWithCheck(id);
    }

    @PostMapping("page")
    @Operation(description = "分页查询自定义字段")
    public Page<CustomFieldDTO> page(@RequestBody CustomFieldPageRequest request) {
        return customFieldService.getCustonFieldpage(request);
    }

}
