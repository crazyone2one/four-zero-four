package cn.master.system.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * @author : 11's papa
 * @since : 2026/5/20, 星期三
 **/
public record PersonalLocaleRequest(
        @Schema(description = "国际化", requiredMode = Schema.RequiredMode.REQUIRED)
        @Pattern(regexp = "(zh-CN)|(en-US)", message = "locale格式不正确")
        @NotEmpty
        String language
) {
}
