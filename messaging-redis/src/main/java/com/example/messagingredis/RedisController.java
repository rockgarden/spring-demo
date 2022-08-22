package com.example.messagingredis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Value("${CHANNEL.cTest}")
    private String channel;

    private RedisTemplate<String, String> redisTemplate;

    public RedisController(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/publish")
    public void publish(@RequestParam String message) {
        // 发送消息
        redisTemplate.convertAndSend(channel, message);
    }
}
