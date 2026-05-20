package cn.master.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @author : 11's papa
 * @since : 2026/5/20, 星期三
 **/
public record ProjectSwitchRequest(
        @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{project.id.not_blank}")
        @Size(min = 1, max = 50, message = "{project.id.length_range}")
        String projectId,
        @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{user.id.not_blank}")
        String userId
) {
}
