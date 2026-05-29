package cn.master.system.service;

import cn.master.system.dto.CustomFieldDTO;
import cn.master.system.dto.request.CustomFieldOptionRequest;
import cn.master.system.dto.request.CustomFieldPageRequest;
import cn.master.system.entity.CustomField;
import cn.master.system.entity.CustomFieldOption;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 自定义字段 服务层。
 *
 * @author 11's papa
 * @since 2026-05-26
 */
public interface CustomFieldService extends IService<CustomField> {
    CustomField baseAdd(CustomField customField, List<CustomFieldOption> options);

    CustomField add(CustomField customField, List<CustomFieldOptionRequest> options);

    CustomFieldDTO getCustomFieldDTO(String id);

    CustomField getWithCheck(String id);

    CustomField update(CustomField customField, List<CustomFieldOptionRequest> options);

    void delete(String id);

    CustomFieldDTO getCustomFieldDTOWithCheck(String id);

    Page<CustomFieldDTO> getCustonFieldpage(CustomFieldPageRequest request);

    List<CustomFieldDTO> listCustomField(String scopeId, String scene);
}
