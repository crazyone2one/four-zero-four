package cn.master.system.dto.project;

import cn.master.system.dto.ProjectBaseRequest;
import cn.master.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateProjectRequest extends ProjectBaseRequest {
    @Schema(description =  "项目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{project.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{project.id.length_range}", groups = {Updated.class})
    private String id;
}
