package cn.master.system.service.impl;

import cn.master.constants.CustomFieldType;
import cn.master.constants.TemplateScene;
import cn.master.exception.FZFException;
import cn.master.system.dto.CustomFieldDTO;
import cn.master.system.dto.request.CustomFieldOptionRequest;
import cn.master.system.dto.request.CustomFieldPageRequest;
import cn.master.system.entity.CustomField;
import cn.master.system.entity.CustomFieldOption;
import cn.master.system.entity.Project;
import cn.master.system.mapper.CustomFieldMapper;
import cn.master.system.service.CustomFieldOptionService;
import cn.master.system.service.CustomFieldService;
import cn.master.system.service.ProjectService;
import cn.master.util.ServiceUtils;
import cn.master.util.Translator;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.master.result.SystemResultCode.*;

/**
 * 自定义字段 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-26
 */
@Service
@RequiredArgsConstructor
public class CustomFieldServiceImpl extends ServiceImpl<CustomFieldMapper, CustomField> implements CustomFieldService {
    private final ProjectService projectService;
    private final CustomFieldOptionService customFieldOptionService;
    private static final String CREATE_USER = "CREATE_USER";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomField baseAdd(CustomField customField, List<CustomFieldOption> options) {
        checkAddExist(customField);
        customField.setEnableOptionKey(BooleanUtils.isTrue(customField.getEnableOptionKey()));
        mapper.insertSelective(customField);
        customFieldOptionService.addByFieldId(customField.getId(), options);
        return customField;
    }

    private void checkAddExist(CustomField customField) {
        boolean exists = queryChain().where(CustomField::getScopeId).eq(customField.getScopeId())
                .and(CustomField::getName).eq(customField.getName())
                .and(CustomField::getScene).eq(customField.getScene())
                .and(CustomField::getFieldKey).eq(customField.getFieldKey())
                .exists();
        if (exists) {
            throw new FZFException(CUSTOM_FIELD_EXIST);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomField add(CustomField customField, List<CustomFieldOptionRequest> options) {
        Project project = projectService.checkResourceExist(customField.getScopeId());
        checkTemplateEnable(project.getOrganizationId(), customField.getScene());
        // customField.setScopeType(TemplateScopeType.PROJECT.name());
        customField.setInternal(false);
        List<CustomFieldOption> customFieldOptions = parseCustomFieldOptionRequest2Option(options);
        // baseAdd(customField, customFieldOptions);
        return baseAdd(customField, customFieldOptions);
    }

    @Override
    public CustomFieldDTO getCustomFieldDTO(String id) {
        CustomFieldDTO customField = getCustomFieldDTOWithCheck(id);
        projectService.checkResourceExist(customField.getScopeId());
        return customField;
    }

    @Override
    public CustomField getWithCheck(String id) {
        checkResourceExist(id);
        return mapper.selectOneById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomField update(CustomField customField, List<CustomFieldOptionRequest> options) {
        CustomField originCustomField = getWithCheck(customField.getId());
        if (originCustomField.getInternal()) {
            // 内置字段不能修改名字
            customField.setName(null);
        }
        customField.setScopeId(originCustomField.getScopeId());
        customField.setScene(originCustomField.getScene());
        Project project = projectService.checkResourceExist(originCustomField.getScopeId());
        checkTemplateEnable(project.getOrganizationId(), originCustomField.getScene());
        checkUpdateExist(customField);
        checkResourceExist(customField.getId());
        mapper.update(customField);
        if (options != null) {
            customFieldOptionService.updateByFieldId(customField.getId(), options);
        }
        return customField;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        CustomField customField = getWithCheck(id);
        checkInternal(customField);
        Project project = projectService.checkResourceExist(customField.getScopeId());
        checkProjectTemplateEnable(project.getOrganizationId(), customField.getScene());
        mapper.deleteById(id);
        customFieldOptionService.deleteByFieldId(id);
    }

    private void checkProjectTemplateEnable(String orgId, String scene) {
        if (isOrganizationTemplateEnable(orgId, scene)) {
            throw new FZFException(PROJECT_TEMPLATE_PERMISSION);
        }
    }

    private boolean isOrganizationTemplateEnable(String orgId, String scene) {
        return false;
    }

    private void checkInternal(CustomField customField) {
        if (customField.getInternal()) {
            throw new FZFException(INTERNAL_CUSTOM_FIELD_PERMISSION);
        }
    }

    private void checkUpdateExist(CustomField customField) {
        if (StringUtils.isBlank(customField.getName())) {
            return;
        }
        boolean exists = queryChain().where(CustomField::getScopeId).eq(customField.getScopeId())
                .and(CustomField::getName).eq(customField.getName())
                .and(CustomField::getScene).eq(customField.getScene())
                .and(CustomField::getId).ne(customField.getId())
                .exists();
        if (exists) {
            throw new FZFException(CUSTOM_FIELD_EXIST);
        }
    }

    @Override
    public CustomFieldDTO getCustomFieldDTOWithCheck(String id) {
        checkResourceExist(id);
        CustomField customField = mapper.selectOneById(id);
        CustomFieldDTO customFieldDTO = new CustomFieldDTO();
        BeanUtils.copyProperties(customField, customFieldDTO);
        customFieldDTO.setOptions(customFieldOptionService.getByFieldId(customFieldDTO.getId()));
        if (customField.getInternal()) {
            customFieldDTO.setInternalFieldKey(customField.getName());
            customField.setName(translateInternalField(customField.getName()));
        }
        projectService.checkResourceExist(customField.getScopeId());
        return customFieldDTO;
    }

    @Override
    public Page<CustomFieldDTO> getCustonFieldpage(CustomFieldPageRequest request) {
        projectService.checkResourceExist(request.getProjectId());
        checkScene(request.getScene());
        Page<CustomFieldDTO> page = queryChain().where(CustomField::getScopeId).eq(request.getProjectId())
                .and(CustomField::getScene).eq(request.getScene())
                .and(CustomField::getName).like(request.getKeyword())
                .orderBy(CustomField::getCreateTime).desc()
                .pageAs(new Page<>(request.getPage(), request.getPageSize()), CustomFieldDTO.class);
        List<CustomFieldDTO> customFields = page.getRecords();
        List<CustomFieldOption> customFieldOptions = customFieldOptionService.getByFieldIds(customFields.stream().map(CustomField::getId).toList());
        Map<String, List<CustomFieldOption>> optionMap = customFieldOptions.stream().collect(Collectors.groupingBy(CustomFieldOption::getFieldId));
        page.getRecords().forEach(item -> {
            item.setOptions(optionMap.get(item.getId()));
            if (CustomFieldType.getHasOptionValueSet().contains(item.getType()) && item.getOptions() == null) {
                item.setOptions(List.of());
            }
            if (Strings.CS.equalsAny(item.getType(), CustomFieldType.MEMBER.name(), CustomFieldType.MULTIPLE_MEMBER.name())) {
                // 成员选项添加默认的选项
                CustomFieldOption createUserOption = new CustomFieldOption();
                createUserOption.setFieldId(item.getId());
                createUserOption.setText(Translator.get("message.domain.createUser"));
                createUserOption.setValue(CREATE_USER);
                createUserOption.setInternal(false);
                item.setOptions(List.of(createUserOption));
            }
        });
        return page;
    }

    @Override
    public List<CustomFieldDTO> listCustomField(String scopeId, String scene) {
        projectService.checkResourceExist(scopeId);
        checkScene(scene);
        List<CustomFieldDTO> lists = queryChain().where(CustomField::getScopeId).eq(scopeId)
                .and(CustomField::getScene).eq(scene).listAs(CustomFieldDTO.class);
        lists.forEach(item -> item.setOptions(customFieldOptionService.getByFieldId(item.getId())));
        return lists;
    }

    @Override
    public List<CustomFieldDTO> listCustomFieldByScene(String scene) {
        checkScene(scene);
        List<CustomFieldDTO> customFields = queryChain().where(CustomField::getScene).eq(scene).listAs(CustomFieldDTO.class);
        customFields.forEach(item -> item.setOptions(customFieldOptionService.getByFieldId(item.getId())));
        return customFields;
    }

    private void checkScene(String scene) {
        Arrays.stream(TemplateScene.values()).map(TemplateScene::name)
                .filter(item -> item.equals(scene))
                .findFirst()
                .orElseThrow(() -> new FZFException(TEMPLATE_SCENE_ILLEGAL));
    }

    private String translateInternalField(String filedName) {
        return Translator.get("custom_field." + filedName);
    }

    private CustomField checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(mapper.selectOneById(id), "permission.organization_custom_field.name");
    }

    private List<CustomFieldOption> parseCustomFieldOptionRequest2Option(List<CustomFieldOptionRequest> options) {
        return options == null ? null : options.stream().map(item -> {
            CustomFieldOption customFieldOption = new CustomFieldOption();
            BeanUtils.copyProperties(item, customFieldOption);
            return customFieldOption;
        }).toList();
    }

    private void checkTemplateEnable(String orgId, String scene) {

    }
}
