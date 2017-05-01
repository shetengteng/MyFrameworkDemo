package com.stt.webConfig;

import java.util.HashSet;
import java.util.Set;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.stt.cache.MyCache;
import com.stt.cache.MyCacheManager;

@Configuration
// 启用缓存
@EnableCaching
@ComponentScan(basePackages = { "com.stt.cache" })
public class MyCacheConfig {

	@Bean
	public CacheManager cacheManager() {
		MyCacheManager cacheManager = new MyCacheManager();
		Set<MyCache> caches = new HashSet<>(1);
		caches.add(new MyCache("myCache"));
		cacheManager.setCaches(caches);
		return cacheManager;
	}

}
