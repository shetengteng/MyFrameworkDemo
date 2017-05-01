package com.stt.utils;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import redis.clients.jedis.Jedis;

import com.stt.utils.RedisUtil.RedisCommandOperation;

/**
 * 分布式锁
 * 
 * @author Administrator
 * 
 */
@Component
public class RedisDistributedLockUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(RedisDistributedLockUtil.class);

	// 尝试次数
	private static Integer loopTimes = 10;
	// 超时时间 ms
	private static Long expireTime = 10 * 1000L;
	// 默认的存储key的redis库
	private static Integer dbIndex = 0;
	// 重试间隔时间
	private static Long retryTime = 400L;

	// 流程:
	// --> setnx 如果锁不存在则获取锁
	// -------返回1 表示获取到锁
	// -------返回0 没有获取到锁
	// ---------- 使用get获取锁的设置时间
	// ---------------是否为空，为空，则进行setnx 操作，否则判断是否超时
	// -----------------没有超时，表示没有获取到锁
	// -----------------超时,使用getset
	// -----------------判断getset获取的旧值是否与先前get的一致，一致，说明获取到锁，否则没有获取到锁

	/**
	 * 获取锁
	 * 
	 * @param key
	 * @return
	 */
	public static Boolean acquireKey(final String key) {
		Boolean isHave = setnxLock(key);
		if (!isHave) {
			// 没有获取到锁,使用get获取锁的时间
			String oldTimeStr = RedisUtil.get(key);

			// 说明锁已经被释放了,重新设置锁
			if (StringUtils.isEmpty(oldTimeStr)) {
				return setnxLock(key);
			}

			final Long oldTime = Long.valueOf(oldTimeStr);
			final Long currentTime = RedisUtil.getTime();

			// 判断是否超时
			if (oldTime + expireTime <= currentTime) {
				// 已经超时，重置锁
				return getsetLock(key, oldTime + "", currentTime + "");
			}

			// 没有超时,获取锁失败
			logger.info("acquireKey failed key:{}", key);
			System.out.println(Thread.currentThread().getName() + "--failed:{}"
					+ key);
			return false;
		}
		return isHave;
	}

	/**
	 * setnx锁操作
	 * 
	 * @param key
	 * @return
	 */
	private static Boolean setnxLock(final String key) {
		return RedisUtil.execute(new RedisCommandOperation<Boolean>() {
			@Override
			public Boolean invoke(Jedis jedis) {
				List<String> time = jedis.time();
				StringBuilder sb = new StringBuilder(time.get(0)).append(time
						.get(1));
				// 转换为ms数，进行setnx
				Long result = jedis.setnx(key, sb.substring(0, sb.length() - 3));
				return result.equals(1L);
			}
		}, dbIndex);
	}

	/**
	 * getset锁操作
	 * 
	 * @param key
	 * @param oldTime
	 * @param currentTime
	 * @return
	 */
	private static Boolean getsetLock(final String key, final String oldTime,
			final String currentTime) {
		return RedisUtil.execute(new RedisCommandOperation<Boolean>() {
			@Override
			public Boolean invoke(Jedis jedis) {
				// 设置新的锁,获取老的锁
				String keyTime = jedis.getSet(key, currentTime);
				if (StringUtils.isEmpty(keyTime)) {
					// 极端情况下，如果keyTime为null，说明有释放锁的操作
					return true;
				}
				if (keyTime.equals(oldTime)) {
					// 表明该锁没有被其他进程获取
					return true;
				} else {
					// 表明该锁已被其他进程获取
					return false;
				}
			}
		}, dbIndex);
	}

	/**
	 * 释放锁
	 * 
	 * @param key
	 */
	public static void releaseKey(String key) {
		RedisUtil.del(key);
	}

	public static <T> T Operation(String key, WorkTask<T> task) {
		Boolean isHaveLock = false;
		try {
			int i = 0;
			for (;;) {
				isHaveLock = acquireKey(key);
				if (isHaveLock) {
					break;
				}
				// 获取loopTimes 次数的锁
				if (i >= loopTimes) {
					break;
				}
				i++;
				try {
					// 睡眠一段时间重试
					Thread.sleep((long) (Math.random() * retryTime));
				} catch (Exception e) {
				}
			}
			if (isHaveLock) {
				// 执行任务
				return task.invoke();
			} else {
				//
				logger.info("Operation failed");
				System.out.println(Thread.currentThread().getName()
						+ "Operation failed times:" + i);
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (isHaveLock == true) {
				releaseKey(key);
			}
		}
	}

	public interface WorkTask<T> {
		T invoke();
	}

}
