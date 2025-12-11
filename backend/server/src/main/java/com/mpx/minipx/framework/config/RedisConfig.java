package com.mpx.minipx.framework.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);  // Spring Boot가 만든 CF를 그대로 사용
    }

    @Bean
    //레디스 연결 정보 로그
    ApplicationRunner showRedisProps(Environment env) {
        return args -> {
            System.out.println(">>> Redis URL=" + env.getProperty("spring.redis.url"));
            System.out.println(">>> Redis HOST=" + env.getProperty("spring.redis.host")
                    + ":" + env.getProperty("spring.redis.port"));
            System.out.println(">>> preferIPv4=" + System.getProperty("java.net.preferIPv4Stack"));
            
            
            System.out.println(">>> Redis URL=" + env.getProperty("spring.data.redis.url"));
            System.out.println(">>> Redis HOST=" + env.getProperty("spring.data.redis.host")
                    + ":" + env.getProperty("spring.data.redis.port"));            
        };
    }

    @Bean
    //레디스 연결 확인 로그
    ApplicationRunner redisSmokeTest(StringRedisTemplate tpl) {
        return args -> {
            try {
                tpl.opsForValue().set("hello", "world");
                System.out.println("REDIS OK: " + tpl.opsForValue().get("hello"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }
}
