package com.knowledge.base.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author fangAndlu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作类型
     */
    String operation() default "";

    /**
     * 操作描述
     */
    String description() default "";
}
