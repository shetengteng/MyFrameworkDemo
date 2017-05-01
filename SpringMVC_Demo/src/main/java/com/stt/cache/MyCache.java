package com.stt.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import com.stt.log.annotation.MyLog;
import com.stt.pojo.Account;

/**
 * 自定义缓存
 */
public class MyCache implements Cache {

	/**
	 * 可以添加redis 客户端进行实现redis缓存
	 */

	private String name;
	private ConcurrentHashMap<String, Account> store = new ConcurrentHashMap<>();

	public MyCache(String name) {
		this.name = name;
	}

	public MyCache() {

	}

	@Override
	public void clear() {
		store.clear();
	}

	@Override
	public void evict(Object key) {
		if (store.get(key.toString()) != null) {
			store.remove(key);
		}
	}

	@Override
	public ValueWrapper get(Object key) {
		System.out.println("----key:" + key);
		Object value = store.get("" + key);
		if (value != null) {
			// 可以对value进行其他的处理
			SimpleValueWrapper result = new SimpleValueWrapper(value);
			System.out.println("----result:" + result.get().toString());
			return result;
		}
		return null;
	}

	@Override
	@MyLog
	public <T> T get(Object key, Class<T> clazz) {
		Object value = store.get(key);
		// value 转换到clazz类型
		if (value != null) {
			try {
				T instance = clazz.newInstance();
				BeanUtils.copyProperties(value, instance);
				return instance;
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object getNativeCache() {
		return store;
	}

	@Override
	public void put(Object key, Object value) {
		store.put("" + key, (Account) value);
		System.out.println("----put" + key + " value:" + value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		return null;
	}

}
