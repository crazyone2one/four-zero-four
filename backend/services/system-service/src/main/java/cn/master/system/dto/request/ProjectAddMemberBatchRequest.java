package cn.master.system.dto.request;

import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectAddMemberBatchRequest extends ProjectAddMemberRequest {
    @Schema(description = "项目ID集合", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<
            @NotBlank(message = "{project.id.not_blank}", groups = {Created.class, Updated.class})
                    String> projectIds;
}
