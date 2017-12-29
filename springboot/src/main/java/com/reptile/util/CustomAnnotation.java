package com.reptile.util;

import java.lang.annotation.*;

/**
 * 自定义注解 用来配置aop
 *
 * @author mrlu
 * @date 2016/10/31
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomAnnotation {

}
