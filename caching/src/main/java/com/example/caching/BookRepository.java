package com.example.caching;

import org.springframework.cache.annotation.CacheConfig;

@CacheConfig(cacheNames = "books")
public interface BookRepository {

	Book getByIsbn(String isbn);

}
