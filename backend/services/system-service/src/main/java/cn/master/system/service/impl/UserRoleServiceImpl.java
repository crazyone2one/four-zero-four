package cn.master.system.service.impl;

import cn.master.exception.FZFException;
import cn.master.system.entity.UserRole;
import cn.master.system.entity.UserRoleRelation;
import cn.master.system.mapper.UserRoleMapper;
import cn.master.system.service.UserRoleService;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.master.system.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 用户组 服务层实现。
 *
 * @author 11's papa
 * @since 2026-05-13
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public void checkRoleIsGlobalAndHaveMember(List<String> roleIdList, boolean isSystem) {
        QueryChain<UserRole> queryChain = queryChain().where(USER_ROLE.ID.in(roleIdList));
        if (isSystem) {
            queryChain.and(USER_ROLE.TYPE.eq("SYSTEM"));
        } else {
            queryChain.and(USER_ROLE.TYPE.eq("GLOBAL"));
        }
        if (queryChain.count() != roleIdList.size()) {
            throw new FZFException(Translator.get("role.not.global"));
        }
    }

    @Override
    public List<UserRole> selectByUserRoleRelations(List<UserRoleRelation> userRoleRelations) {
        List<String> roleIds = userRoleRelations.stream().map(UserRoleRelation::getRoleId).toList();
        return mapper.selectListByIds(roleIds);
    }
}
