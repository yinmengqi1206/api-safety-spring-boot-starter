package com.ymq.api.safety;

/**
 * @Author yinmengqi
 * @Date 2024/6/24 11:10
 * @Version 1.0
 */
public class ApiSafetyException extends RuntimeException {
    public ApiSafetyException(String message) {
        super(message);
    }
}
