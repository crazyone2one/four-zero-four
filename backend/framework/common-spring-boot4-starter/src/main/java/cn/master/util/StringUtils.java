package cn.master.util;

import org.apache.commons.lang3.Strings;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
public class StringUtils {
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        return Strings.CS.equals(cs1, cs2);
    }
    public static boolean equalsIgnoreCase(final CharSequence cs1, final CharSequence cs2) {
        return Strings.CI.equals(cs1, cs2);
    }
}
