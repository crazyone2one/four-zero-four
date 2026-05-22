package cn.master.security.util;

import cn.master.security.context.SecurityUser;
import cn.master.util.LogUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author : 11's papa
 * @since : 2026/5/11, 星期一
 **/
public class SecurityUtils {
    private static final ThreadLocal<String> projectId = new ThreadLocal<>();
    private static final ThreadLocal<String> organizationId = new ThreadLocal<>();

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

    public static void setCurrentOrganizationId(String organizationId) {
        SecurityUtils.organizationId.set(organizationId);
    }

    public static String getCurrentOrganizationId() {
        if (StringUtils.isNotEmpty(organizationId.get())) {
            return organizationId.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            LogUtils.debug("ORGANIZATION: {}", request.getHeader("ORGANIZATION"));
            if (request.getHeader("ORGANIZATION") != null) {
                return request.getHeader("ORGANIZATION");
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return Objects.requireNonNull(getCurrentUser()).getOrganizationId();
    }

    public static void setCurrentProjectId(String projectId) {
        SecurityUtils.projectId.set(projectId);
    }

    public static String getCurrentProjectId() {
        if (StringUtils.isNotEmpty(projectId.get())) {
            return projectId.get();
        }
        try {
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            LogUtils.debug("PROJECT: {}", request.getHeader("PROJECT"));
            if (request.getHeader("PROJECT") != null) {
                return request.getHeader("PROJECT");
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return Objects.requireNonNull(getCurrentUser()).getProjectId();
    }
}
