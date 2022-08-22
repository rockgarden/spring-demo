package com.example.cachingbyehcache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CachingEhcacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(CachingEhcacheApplication.class, args);
	}

}
