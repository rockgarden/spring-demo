package com.example.cachingbyredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CachingByRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(CachingByRedisApplication.class, args);
	}

}
