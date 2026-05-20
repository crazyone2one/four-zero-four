package cn.master.system.dto.project;

import cn.master.system.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class ProjectDTO extends Project implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "项目成员数量", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long memberCount;
    @Schema(description = "所属组织", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String organizationName;
}
