package cn.master.system.dto.user;

import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRoleRelation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class UserDTO extends UserBaseVO {
    private List<UserRole> userRoles = new ArrayList<>();
    private List<UserRoleRelation> userRoleRelations = new ArrayList<>();
    private List<UserRoleResourceDTO> userRolePermissions = new ArrayList<>();
}
