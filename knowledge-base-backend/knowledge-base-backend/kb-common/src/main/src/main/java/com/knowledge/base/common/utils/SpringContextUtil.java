package com.knowledge.base.common.utils;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * 在 Spring 框架中，我们通常通过 @Autowired 或 @Resource 注解来注入（获取）Bean。但这只适用于由 Spring 管理的类。
 * 如果你在一个非 Spring 管理的类中（例如普通的工具类、静态方法、某些框架底层的拦截器或回调函数中），
 * 你是无法直接使用 @Autowired 的。这时候，就需要用到这个工具类。
 * 它相当于一个“全局的 Bean 仓库”，允许你在代码的任何地方，通过手动调用的方式获取 Spring 容器中的 Bean。
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    /**
     * 根据类型获取 Bean
     * @param clazz Bean 的类型
     * @param <T>   泛型
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据名称获取 Bean
     * @param beanName Bean 的名称
     * @return Bean实例
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据名称和类型获取 Bean
     * @param name  Bean 的名称
     * @param clazz Bean 的类型
     * @param <T>   泛型
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }
}
