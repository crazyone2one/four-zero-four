package cn.master.system.entity;

import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.handler.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 定时任务 实体类。
 *
 * @author 11's papa
 * @since 2026-05-22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "定时任务")
@Table("schedule")
public class Schedule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Schema(description = "", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{schedule.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{schedule.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    /**
     * 业务ID
     */
    @Schema(description = "业务ID")
    private Long num;

    @Schema(description = "qrtz UUID")
    private String key;

    @Schema(description = "执行类型 cron", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{schedule.type.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{schedule.type.length_range}", groups = {Created.class, Updated.class})
    private String type;

    @Schema(description = "cron 表达式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{schedule.value.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{schedule.value.length_range}", groups = {Created.class, Updated.class})
    private String value;

    @Schema(description = "Schedule Job Class Name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{schedule.job.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 64, message = "{schedule.job.length_range}", groups = {Created.class, Updated.class})
    private String job;

    @Schema(description = "资源类型 API/TESL_PLAN", requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = 1, max = 50, message = "{schedule.resource_type.length_range}", groups = {Created.class, Updated.class})
    private String resourceType;

    /**
     * 资源ID
     */
    @Schema(description = "资源ID")
    private String resourceId;

    /**
     * 是否开启
     */
    @Schema(description = "是否开启")
    private Boolean enable;

    /**
     * 项目ID
     */
    @Schema(description = "项目ID")
    private String projectId;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 配置
     */
    @Schema(description = "配置")
    @Column(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> config;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;

    /**
     * 创建时间
     */
    @Column(onInsertValue = "now()")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
