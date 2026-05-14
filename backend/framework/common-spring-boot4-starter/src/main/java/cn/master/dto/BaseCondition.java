package cn.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@Data
public class BaseCondition {
    @Schema(description = "关键字")
    private String keyword;
    private String projectId;
    private String sensorType;
    private String sensorGroup;
}
