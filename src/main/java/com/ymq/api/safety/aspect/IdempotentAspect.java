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
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author yinmengqi
 * @Date 2024/6/24 11:29
 * @Version 1.0
 */
@Aspect
@AllArgsConstructor
@Slf4j
@Component
public class IdempotentAspect {

    @PostConstruct
    public void init() {
        log.info("IdempotentAspect 初始化中...");
    }

    private final StringRedisTemplate redisTemplate;

    private final ApiSafetyProperties apiSafetyProperties;

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    @Around("@annotation(Idempotent)")
    public Object idempotent(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!apiSafetyProperties.getIdempotent().isEnable()) {
            // 处理请求
            return joinPoint.proceed();
        }
        // 获取请求方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 获取注解信息
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        String key = generateKeyBySpEL(idempotent.key(), joinPoint);
        if (key == null) {
            log.info("key is null 不做幂等处理");
            // 处理请求
            return joinPoint.proceed();
        }
        String redisKey = idempotent.prefix() + ":idempotent:" + key;
        Boolean setIfAbsent = redisTemplate.opsForValue().setIfAbsent(redisKey, "1", idempotent.expire(), TimeUnit.SECONDS);
        // 判断是否已经请求过
        if (Boolean.FALSE.equals(setIfAbsent)) {
            throw new ApiSafetyException(idempotent.msg());
        }
        // 处理请求
        return joinPoint.proceed();
    }

    public String generateKeyBySpEL(String key, ProceedingJoinPoint pjp) {
        Expression expression = PARSER.parseExpression(key);
        EvaluationContext context = new StandardEvaluationContext();
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Object[] args = pjp.getArgs();
        String[] paramNames = PARAMETER_NAME_DISCOVERER.getParameterNames(methodSignature.getMethod());
        for (int i = 0; i < args.length; i++) {
            if (paramNames != null) {
                context.setVariable(paramNames[i], args[i]);
            }
        }
        return Optional.ofNullable(expression.getValue(context)).map(Object::toString).orElse(null);
    }
}
