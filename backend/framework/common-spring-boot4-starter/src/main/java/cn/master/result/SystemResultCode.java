package cn.master.result;

import cn.master.exception.IResultCode;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
public enum SystemResultCode implements IResultCode {
    USER_TOO_MANY(101511, "User too many"),
    DEPT_USER_TOO_MANY(101512, "Department user too many"),
    INVITE_EMAIL_EXIST(101513, "user_email_already_exists"),
    USER_ROLE_RELATION_EXIST(100002, "user_role_relation_exist_error"),
    NO_GLOBAL_USER_ROLE_PERMISSION(101013, "no_global_user_role_permission_error"),
    GLOBAL_USER_ROLE_PERMISSION(101001, "global_user_role_permission_error"),
    GLOBAL_USER_ROLE_EXIST(101002, "global_user_role_exist_error"),
    INTERNAL_USER_ROLE_PERMISSION(100003, "internal_user_role_permission_error"),
    USER_ROLE_RELATION_REMOVE_ADMIN_USER_PERMISSION(100004, "user_role_relation_remove_admin_user_permission_error"),
    PROJECT_TEMPLATE_PERMISSION(102002, "project_template_permission_error"),
    CUSTOM_FIELD_EXIST(100012, "custom_field.exist"),
    INTERNAL_CUSTOM_FIELD_PERMISSION(100008, "internal_custom_field_permission_error"),
    TEMPLATE_SCENE_ILLEGAL(100010, "template_scene_illegal_error"),
    ADMIN_USER_ROLE_PERMISSION(100019, "internal_admin_user_role_permission_error");
    private final int code;
    private final String message;

    SystemResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return getTranslationMessage(this.message);
    }
}
