package com.stt.cache;

import java.util.Collection;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

import com.stt.log.annotation.MyLog;

/**
 * 实现自定义的CacheManager
 * 
 * @author Administrator
 * 
 */
public class MyCacheManager extends AbstractCacheManager {

	private Collection<? extends MyCache> caches;

	@Override
	protected Collection<? extends Cache> loadCaches() {
		return this.caches;
	}

	/**
	 * 设置自定义缓存
	 * 
	 * @param caches
	 */
	@MyLog
	public void setCaches(Collection<? extends MyCache> caches) {
		this.caches = caches;
	}

}
