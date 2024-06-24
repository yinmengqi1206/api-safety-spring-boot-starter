package com.ymq.api.safety.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.ymq.api.safety.ApiSafetyConstant.REDIS_PREFIX;

/**
 * @Author yinmengqi
 * @Date 2024/6/24 11:29
 * @Version 1.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    //支持spel表达式
    String key();

    //幂等时长
    int expire() default 5;

    //提示
    String msg() default "请勿重复提交";

    String prefix() default REDIS_PREFIX;
}
