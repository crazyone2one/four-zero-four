package cn.master.system.log.annotation;

import cn.master.constants.OperationLogType;

import java.lang.annotation.*;

/**
 * @author : 11's papa
 * @since : 2026/5/21, 星期四
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    OperationLogType type() default OperationLogType.SELECT;

    String expression();

    Class[] clazz() default {};
}
