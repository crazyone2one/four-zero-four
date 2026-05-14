package cn.master.quartz.util;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
public class ClassUtils {
    public static Class<?>[] toClass(final Object... array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new Class[0];
        }
        final Class<?>[] classes = new Class[array.length];
        for (int i = 0; i < array.length; i++) {
            classes[i] = array[i] == null ? null : array[i].getClass();
        }
        return classes;
    }
}
