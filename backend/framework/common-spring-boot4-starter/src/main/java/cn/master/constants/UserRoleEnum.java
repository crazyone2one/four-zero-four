package cn.master.constants;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
public enum UserRoleEnum {
    GLOBAL("global");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
