package cn.master.system.dto.request;

import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * @author : 11's papa
 * @since : 2026/6/10, 星期三
 **/
public record ProjectParamsRequest(
        @NotBlank(message = "{project_parameters.id.not_blank}", groups = {Updated.class})
        @Size(min = 1, max = 50, message = "{project_parameters.id.length_range}", groups = {Updated.class})
        String id,
        @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{project_parameters.project_id.not_blank}", groups = {Created.class, Updated.class})
        @Size(min = 1, max = 50, message = "{project_parameters.project_id.length_range}", groups = {Created.class, Updated.class})
        String projectId,
        Map<String, Object> parameters,
        String paramType) {
}
