package com.reptile.util;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2017/5/16.
 *自定义注解
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomAnnotation {

}
