package cn.master.system.dto.project;

import cn.master.dto.BasePageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/5/21, 星期四
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectRequest extends BasePageRequest {
    @Schema(description = "组织ID")
    private String organizationId;
}
