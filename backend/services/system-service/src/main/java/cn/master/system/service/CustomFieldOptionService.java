package cn.master.system.service;

import cn.master.system.dto.request.CustomFieldOptionRequest;
import cn.master.system.entity.CustomFieldOption;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 自定义字段选项 服务层。
 *
 * @author 11's papa
 * @since 2026-05-26
 */
public interface CustomFieldOptionService extends IService<CustomFieldOption> {

    void addByFieldId(String fieldId, List<CustomFieldOption> customFieldOptions);

    List<CustomFieldOption> getByFieldId(String fieldId);

    void updateByFieldId(String fieldId, List<CustomFieldOptionRequest> customFieldOptionRequests);

    void deleteByFieldId(String fieldId);

    List<CustomFieldOption> getByFieldIds(List<String> fieldIds);
}
