package com.example.messagingredis;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageSubscriber {

    public MessageSubscriber(RedisTemplate<String, String> redisTemplate, @Value("${CHANNEL.cTest}") String channel) {

        RedisConnectionFactory cf = redisTemplate.getConnectionFactory();
        if (cf != null) {
            RedisConnection redisConnection = cf.getConnection();
            redisConnection.subscribe((message, bytes) -> log.info("Receive message : {}", message),
                    channel.getBytes(StandardCharsets.UTF_8));
        } else {
            log.error("redis connect failure");
        }
    }

}
