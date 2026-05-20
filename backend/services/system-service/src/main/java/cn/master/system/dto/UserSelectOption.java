package cn.master.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @author : 11's papa
 * @since : 2026/5/20, 星期三
 **/
public record UserSelectOption(
        @Schema(description = "节点唯一ID")
        String id,
        @Schema(description = "节点名称")
        String name,
        @Schema(description = "是否选中")
        boolean selected ,
        @Schema(description = "是否允许关闭")
        boolean closeable
) {
}
