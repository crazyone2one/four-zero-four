package cn.master.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchProcessResponse {
    @Schema(description = "全部数量")
    private long totalCount;
    @Schema(description = "成功数量")
    private long successCount;
}
