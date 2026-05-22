package cn.master.security.context;

import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class SecurityUser implements UserDetails {
    @Getter
    private final String userId;
    @Getter
    private final String projectId;
    @Getter
    private final String organizationId;
    private final String username;
    private final String password;
    private final List<String> roles;

    public SecurityUser(String userId, String username, String password,
                        String projectId, String organizationId,
                        List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.roles = roles;
        this.projectId = projectId;
        this.organizationId = organizationId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

}
