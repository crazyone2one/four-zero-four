package cn.master.system.dto.user;

import cn.master.dto.BatchProcessDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserChangeEnableRequest extends BatchProcessDTO {
    @Schema(description = "禁用/启用", requiredMode = Schema.RequiredMode.REQUIRED)
    boolean enable;
}
