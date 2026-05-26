package cn.master.system.dto.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : 11's papa
 * @since : 2026/5/25, 星期一
 **/
@Data
@Schema(description =  "权限信息")
public class Permission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description =  "权限ID")
    private String id;
    @Schema(description =  "权限名称")
    private String name;
    @Schema(description =  "是否启用该权限")
    private Boolean enable = false;
    @Schema(description =  "是否是企业权限")
    private Boolean license = false;
}
