package cn.master.system.dto.permission;

import lombok.Data;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/25, 星期一
 **/
@Data
public class PermissionCache {
    private List<PermissionDefinitionItem> permissionDefinition;
}

