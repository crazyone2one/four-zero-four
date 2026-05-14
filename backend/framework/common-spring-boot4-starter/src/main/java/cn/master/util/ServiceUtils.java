package cn.master.util;

import cn.master.exception.FZFException;
import cn.master.result.HttpResultCode;

/**
 * @author : 11's papa
 * @since : 2026/5/13, 星期三
 **/
public class ServiceUtils {
    private static final ThreadLocal<String> resourceName = new ThreadLocal<>();

    public static <T> T checkResourceExist(T resource, String name) {
        if (resource == null) {
            resourceName.set(name);
            throw new FZFException(HttpResultCode.NOT_FOUND);
        }
        return resource;
    }

    public static String getResourceName() {
        return resourceName.get();
    }

    public static void clearResourceName() {
        resourceName.remove();
    }
}
