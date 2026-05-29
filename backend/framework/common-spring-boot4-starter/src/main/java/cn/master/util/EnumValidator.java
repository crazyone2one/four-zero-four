package cn.master.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author : 11's papa
 * @since : 2026/5/28, 星期四
 **/
public class EnumValidator {
    public static <E extends Enum<E>> E validateEnum(Class<E> enumClass, String value) {
        if (StringUtils.isBlank(value)) {
            LogUtils.error("Invalid value for enum " + enumClass.getSimpleName() + ": " + value);
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException e) {
            LogUtils.error("Invalid value for enum " + enumClass.getSimpleName() + ": " + value, e);
            return null;
        }
    }
}
