package cn.master.system.log.dto;

import cn.master.system.entity.OperationLog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class LogDTO extends OperationLog {
    @Schema(description =  "是否需要历史记录")
    private Boolean history = false;
    public LogDTO() {
    }

    public LogDTO(String projectId, String organizationId, String sourceId, String createUser, String type, String module, String content) {
        this.setProjectId(projectId);
        this.setOrganizationId(organizationId);
        this.setSourceId(sourceId);
        this.setCreateUser(createUser);
        this.setType(type);
        this.setModule(module);
        this.setContent(content);
        this.setCreateTime(LocalDateTime.now());
    }
}
