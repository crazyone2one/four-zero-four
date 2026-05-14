package cn.master.system.dto.user;

import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
public record UserBatchCreateRequest(
        @Schema(description = "用户信息集合", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(groups = {Created.class, Updated.class}, message = "{user.info.not_empty}")
        List<@Valid UserCreateInfo> userInfoList,
        @Schema(description = "用户组", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty(groups = {Created.class, Updated.class}, message = "{user_role.id.not_blank}")
        List<@Valid @NotBlank(message = "{user_role.id.not_blank}", groups = {Created.class, Updated.class}) String> userRoleIdList
) {
}
