package cn.master.system.dto.user;

import cn.master.system.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UserTableVO extends UserBaseVO {
    @Schema(description = "用户所属用户组")
    private List<UserRole> userRoleList = new ArrayList<>();

    public void setUserRole(UserRole userRole) {
        if (!userRoleList.contains(userRole)) {
            userRoleList.add(userRole);
        }
    }
}
