package cn.master.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * @author : 11's papa
 * @since : 2026/5/21, 星期四
 **/
public record ProjectMemberRequest(
        @Schema(description = "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{project.id.not_blank}")
        String projectId,
        @Min(value = 5, message = "每页显示条数必须不小于5")
        @Max(value = 500, message = "每页显示条数不能大于500")
        @Schema(description = "每页显示条数")
        int pageSize,
        @Min(value = 1, message = "当前页码必须大于0")
        @Schema(description = "当前页码")
        int page,
        @Schema(description =  "关键字")
        String keyword
) {
}
