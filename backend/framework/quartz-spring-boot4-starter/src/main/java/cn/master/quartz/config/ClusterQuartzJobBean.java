package cn.master.quartz.config;

import cn.master.quartz.util.ClassUtils;
import cn.master.quartz.util.QuartzBeanFactory;
import lombok.Setter;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.lang.reflect.Method;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@PersistJobDataAfterExecution
public class ClusterQuartzJobBean extends QuartzJobBean {
    private final Logger logger = LoggerFactory.getLogger(ClusterQuartzJobBean.class);

    @Setter
    private String targetObject;

    @Setter
    private String targetMethod;

    private Object[] params;

    public void setParams(Object... params) {
        this.params = params;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            logger.debug("The scheduled task starts：targetObject={}, targetMethod={}", targetObject, targetMethod);
            Object targetObject = QuartzBeanFactory.getBean(this.targetObject);
            Method m = targetObject.getClass().getMethod(targetMethod, ClassUtils.toClass(params));
            m.invoke(targetObject, params);
            logger.debug("The scheduled task ends normally：targetObject={}, targetMethod={}", this.targetObject, targetMethod);
        } catch (final Exception e) {
            logger.error("The scheduled task execution failed：targetObject={}, targetMethod={}", targetObject, targetMethod, e);
        }
    }
}
