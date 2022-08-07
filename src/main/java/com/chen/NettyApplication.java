package com.chen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@MapperScan(basePackages = {"com.chen.mapper.extend"})
public class NettyApplication {
    
    public static void main(String[] args) {
        
        ApplicationContext context = SpringApplication.run(NettyApplication.class, args);
        
        RedisTemplate redis = context.getBean("redisTemplate", RedisTemplate.class);
        
        ServerStartup startup = context.getBean(ServerStartup.class);
        
        try {
            startup.startServer(redis);
        } catch (Exception e) {
        
        }
    }
}

 
