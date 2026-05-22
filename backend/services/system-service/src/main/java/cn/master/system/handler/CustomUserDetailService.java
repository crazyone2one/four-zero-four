package cn.master.system.handler;

import cn.master.security.context.SecurityUser;
import cn.master.system.entity.SystemUser;
import cn.master.system.entity.UserRoleRelation;
import cn.master.util.MultiCacheManager;
import cn.master.util.Translator;
import com.mybatisflex.core.query.QueryChain;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;

import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/12, 星期二
 **/
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final MultiCacheManager multiCacheManager;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemUser systemUser = QueryChain.of(SystemUser.class).where(SystemUser::getName).eq(username)
                .oneOpt()
                .orElseThrow(() -> new UsernameNotFoundException(Translator.get("user_not_exist")));
        List<String> roles = multiCacheManager.get("user:roles:" + systemUser.getId(),
                new TypeReference<>() {
                },
                () -> QueryChain.of(UserRoleRelation.class)
                        .where(UserRoleRelation::getUserId).eq(systemUser.getId())
                        .list().stream().map(UserRoleRelation::getRoleId).toList());
        return new SecurityUser(systemUser.getId(), systemUser.getName(), systemUser.getPassword(),
                systemUser.getLastProjectId(), systemUser.getLastOrganizationId(), roles);
    }
}
