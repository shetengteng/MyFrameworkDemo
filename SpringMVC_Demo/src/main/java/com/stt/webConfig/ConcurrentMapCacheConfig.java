package com.stt.webConfig;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 启用缓存
@EnableCaching
public class ConcurrentMapCacheConfig {

	// 使用Spring自带的采用ConcurrentMap实现的缓存机制
	@Bean
	public CacheManager cacheManager() {
		// 定义缓存的名称
		return new ConcurrentMapCacheManager("concurrentCache");
	}
}
