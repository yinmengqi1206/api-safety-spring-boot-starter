package com.ymq.api.safety.aspect;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.ymq.api.safety.ApiSafetyConstant.REDIS_PREFIX;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Author yinmengqi
 * @Date 2024/6/23 17:20
 * @Version 1.0
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {

    //限制时间窗口间隔长度，默认10秒
    int seconds() default 10;

    //上述时间窗口内允许的最大请求数量，默认为5次
    int maxCount() default 5;

    //提示
    String msg() default "您操作频率太过频繁，稍后再试试";

    String prefix() default REDIS_PREFIX;
}
