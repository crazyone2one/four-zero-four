package cn.master.constants;

import lombok.Getter;

/**
 * @author : 11's papa
 * @since : 2026/5/19, 星期二
 **/
@Getter
public enum InternalUserRole {
    ADMIN("admin"),
    MEMBER("member"),
    ORG_ADMIN("org_admin"),
    ORG_MEMBER("org_member"),
    PROJECT_ADMIN("project_admin"),
    PROJECT_MEMBER("project_member");

    private final String value;

    InternalUserRole(String value) {
        this.value = value;
    }

}
