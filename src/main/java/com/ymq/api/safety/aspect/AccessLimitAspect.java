package com.ymq.api.safety.aspect;

import com.ymq.api.safety.ApiSafetyException;
import com.ymq.api.safety.ApiSafetyProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author yinmengqi
 * @Date 2024/6/23 17:43
 * @Version 1.0
 */
@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class AccessLimitAspect {

    @PostConstruct
    public void init() {
        log.info("AccessLimitAspect 初始化中...");
    }

    private final RedisTemplate<String, Integer> redisTemplate;

    private final ApiSafetyProperties apiSafetyProperties;

    @Around("@annotation(AccessLimit)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!apiSafetyProperties.getAccessLimit().isEnable()) {
            // 处理请求
            return joinPoint.proceed();
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getName();
        AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
        log.info("开始限流处理 accessLimit:{}", accessLimit);
        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        String msg = accessLimit.msg();
        String redisKey = accessLimit.prefix() + ":accessLimit:" + className + ":" + method.getName();
        Long increment = redisTemplate.opsForValue().increment(redisKey);
        if (Objects.equals(increment, 1L)) {
            redisTemplate.expire(redisKey, seconds, TimeUnit.SECONDS);
        }
        if (increment > maxCount) {
            throw new ApiSafetyException(msg);
        }
        return joinPoint.proceed();
    }
}
