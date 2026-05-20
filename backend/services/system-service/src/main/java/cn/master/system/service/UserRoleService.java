package cn.master.system.service;

import cn.master.system.dto.UserSelectOption;
import cn.master.system.entity.UserRoleRelation;
import com.mybatisflex.core.service.IService;
import cn.master.system.entity.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * 用户组 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface UserRoleService extends IService<UserRole> {
    void checkRoleIsGlobalAndHaveMember(@Valid @NotEmpty List<String> roleIdList, boolean isSystem);

    List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations);

    List<UserSelectOption> getGlobalSystemRoleList();
}
