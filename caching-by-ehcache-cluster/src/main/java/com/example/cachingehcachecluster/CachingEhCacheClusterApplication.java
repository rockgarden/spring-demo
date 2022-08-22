package com.example.cachingehcachecluster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CachingEhCacheClusterApplication {

	public static void main(String[] args) throws Exception {
		// LocateRegistry.createRegistry(Integer.valueOf(System.getProperty("rmi.port")));
		SpringApplication.run(CachingEhCacheClusterApplication.class, args);
	}

}