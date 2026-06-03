package cn.master.quartz.config;

import cn.master.quartz.util.QuartzBeanFactory;
import lombok.Setter;
import org.jspecify.annotations.NonNull;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@Setter
@PersistJobDataAfterExecution
public class ClusterQuartzJobBean extends QuartzJobBean {
    private final Logger logger = LoggerFactory.getLogger(ClusterQuartzJobBean.class);

    private String targetObject;
    private String targetMethod;
    private Object[] params;

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        try {
            logger.debug("The scheduled task starts：targetObject={}, targetMethod={}", targetObject, targetMethod);
            Object targetBean = QuartzBeanFactory.getBean(this.targetObject);
            Method method = findMatchingMethod(targetBean);

            if (method != null) {
                Class<?>[] paramTypes = method.getParameterTypes();
                ReflectionUtils.makeAccessible(method);
                if (paramTypes.length == 0) {
                    ReflectionUtils.invokeMethod(method, targetBean);
                } else {
                    ReflectionUtils.invokeMethod(method, targetBean, params);
                }
                logger.debug("The scheduled task ends normally：targetObject={}, targetMethod={}", this.targetObject, targetMethod);
            } else {
                logger.error("No matching method：targetObject={}, targetMethod={}", targetObject, targetMethod);
            }
        } catch (final Exception e) {
            logger.error("The scheduled task execution failed：targetObject={}, targetMethod={}", targetObject, targetMethod, e);
        }
    }

    private Method findMatchingMethod(Object targetBean) {
        Class<?> targetClass = targetBean.getClass();
        if (params == null || params.length == 0) {
            try {
                return targetClass.getMethod(targetMethod);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (!method.getName().equals(targetMethod)) {
                continue;
            }

            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == 0) {
                try {
                    return targetClass.getMethod(targetMethod);
                } catch (NoSuchMethodException e) {
                    return null;
                }
            }
            if (paramTypes.length != params.length) {
                continue;
            }
            if (isAssignable(paramTypes, params)) {
                return method;
            }
        }

        return null;
    }

    private boolean isAssignable(Class<?>[] paramTypes, Object[] args) {
        for (int i = 0; i < paramTypes.length; i++) {
            if (args[i] == null) {
                if (paramTypes[i].isPrimitive()) {
                    return false;
                }
                continue;
            }

            if (!paramTypes[i].isAssignableFrom(args[i].getClass())) {
                return false;
            }
        }
        return true;
    }
}
