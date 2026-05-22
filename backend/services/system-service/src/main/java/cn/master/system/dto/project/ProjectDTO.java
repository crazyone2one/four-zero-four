package cn.master.system.dto.project;

import cn.master.system.dto.user.UserExtendDTO;
import cn.master.system.entity.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
    @Schema(description = "管理员", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<UserExtendDTO> adminList;
    @Schema(description = "创建人是否是管理员", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean projectCreateUserIsAdmin;
}
