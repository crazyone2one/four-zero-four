package cn.master.security.util;

import cn.master.security.context.SecurityUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class SecurityUtils {
    public static SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        // 检查是否已认证且principal是SecurityUser类型
        Object principal = authentication.getPrincipal();
        if (principal instanceof SecurityUser) {
            return (SecurityUser) principal;
        }
        return null;
    }

    public static String getUsername() {
        SecurityUser securityUser = getCurrentUser();
        return securityUser == null ? null : securityUser.getUsername();
    }

    public static String getUserId() {
        SecurityUser securityUser = getCurrentUser();
        return securityUser == null ? null : securityUser.getUserId();
    }
}
