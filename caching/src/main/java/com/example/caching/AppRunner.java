package com.example.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

	private final BookRepository bookRepository;

	@Autowired
    private CacheManager cacheManager;

	public AppRunner(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		String s1 = "isbn-1234";
		logger.info("CacheManager type : {}", cacheManager.getClass());
		logger.info(".... Fetching books");
		logger.info("{} --> {}", s1, bookRepository.getByIsbn(s1));
		logger.info("isbn-4567 --> {}", bookRepository.getByIsbn("isbn-4567"));
		logger.info("{} --> {}", s1, bookRepository.getByIsbn(s1));
		logger.info("isbn-4567 --> {}", bookRepository.getByIsbn("isbn-4567"));
		logger.info("isbn-1234 --> {}", bookRepository.getByIsbn(s1));
		logger.info("isbn-1234 --> {}", bookRepository.getByIsbn(s1));
	}

}
