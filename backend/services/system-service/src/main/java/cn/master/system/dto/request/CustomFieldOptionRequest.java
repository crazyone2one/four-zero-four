package cn.master.system.dto.request;

import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author : 11's papa
 * @since : 2026/5/26, 星期二
 **/
public record CustomFieldOptionRequest(
        @Schema(title = "选项值", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{custom_field_option.value.not_blank}", groups = {Created.class, Updated.class})
        @Size(min = 1, max = 50, message = "{custom_field_option.value.length_range}", groups = {Created.class, Updated.class})
        String value,
        @Schema(title = "选项值名称", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "{custom_field_option.text.not_blank}", groups = {Created.class, Updated.class})
        @Size(min = 1, max = 255, message = "{custom_field_option.text.length_range}", groups = {Created.class, Updated.class})
        String text,
        @Schema(title = "选项值顺序", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "{custom_field_option.pos.not_blank}", groups = {Created.class})
        Integer pos
) {
}
