package com.ymq.api.safety;

import com.ymq.api.safety.aspect.AccessLimitAspect;
import com.ymq.api.safety.aspect.IdempotentAspect;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * @Author yinmengqi
 * @Date 2024/6/23 17:28
 * @Version 1.0
 */
@AutoConfiguration
@ConditionalOnClass(RedisAutoConfiguration.class)
@EnableConfigurationProperties(ApiSafetyProperties.class)
@AllArgsConstructor
@Import({AccessLimitAspect.class, IdempotentAspect.class})
public class ApiSafetyAutoConfiguration {
}
