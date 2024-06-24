package com.ymq.api.safety;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yinmengqi
 * @Date 2024/6/23 12:35
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties(prefix = "api.safety")
@Data
public class ApiSafetyProperties {

    //限流开关
    private Enable accessLimit = new Enable();

    //幂等开关
    private Enable idempotent = new Enable();

    @Data
    public static class Enable {
        private boolean enable = true;
    }
}
