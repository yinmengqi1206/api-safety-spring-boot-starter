# api-safety-spring-boot-starter

提供基于Redis的接口限流、幂等

# 使用方式

```java
<dependency>
    <groupId>com.ymq.api.safety</groupId>
    <artifactId>api-safety-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

支持配置全局开关功能

```yaml
api:
  safety:
    # 开启限流，默认开
    accessLimit:
      enable: true
    # 开启幂等，默认开
    idempotent:
      enable: true
```

# 使用前提

```java
依赖模块:spring-boot-starter-data-redis
```

# 功能注解

## 限流（@AccessLimit）

```java

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
```

## 幂等（@Idempotent）

key使用方式为spel表达式，具体使用方式自行学习了解

```java

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
```