package cn.master.system.service;

import cn.master.system.dto.user.UserTableVO;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRoleRelation;
import cn.master.validation.groups.Created;
import cn.master.validation.groups.Updated;
import com.mybatisflex.core.service.IService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;

/**
 * 用户组关系 服务层。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
public interface UserRoleRelationService extends IService<UserRoleRelation> {
    void addUserRoleRelation(List<String> userIds, List<String> roleIds);

    void deleteByUserIdList(List<String> userIdList);

    void updateUserSystemGlobalRole(@Valid SystemUser user, @Valid @NotEmpty String operator, @Valid @NotEmpty List<String> roleList);

    Map<String, UserTableVO> selectGlobalUserRoleAndOrganization(List<String> userIdList);

    List<UserRoleRelation> selectByUserId(String userId);
}
