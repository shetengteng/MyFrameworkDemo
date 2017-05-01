package com.stt.utils;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Component
public class RedisUtil {
	private static final Logger logger = LoggerFactory
			.getLogger(RedisUtil.class);

	// 主从访问
	// @Autowired
	// private static JedisSentinelPool jedisPool;

	// 单机访问
	private static JedisPool jedisPool;

	// 静态变量不能使用autowired直接注入
	@Autowired
	public void setJedisPool(JedisPool jedisPool) {
		RedisUtil.jedisPool = jedisPool;
	}

	/**
	 * 获取资源
	 * 
	 * @return
	 */
	public static Jedis getInstance() {
		if (jedisPool == null) {
			return null;
		}
		int timeoutCount = 0;
		while (true) // 如果是网络超时则多试几次
		{
			try {
				Jedis jedis = jedisPool.getResource();
				return jedis;
			} catch (Exception e) {
				// 底层原因是SocketTimeoutException，不过redis已经捕捉且抛出JedisConnectionException，不继承于前者
				if (e instanceof JedisConnectionException
						|| e instanceof SocketTimeoutException) {
					timeoutCount++;
					logger.warn("getJedis timeoutCount={}", timeoutCount);
					if (timeoutCount > 3) {
						break;
					}
				} else {
					logger.warn("jedisInfo:NumActive="
							+ jedisPool.getNumActive() + ", NumIdle="
							+ jedisPool.getNumIdle() + ", NumWaiters="
							+ jedisPool.getNumWaiters() + ", isClosed="
							+ jedisPool.isClosed());
					logger.error("JedisConnectionException ERROR:{}", e);
					break;
				}
			}
		}
		return null;
	}

	/**
	 * 释放资源
	 * 
	 * @param jedis
	 */
	public static void release(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * 测试的时候使用，释放所有资源
	 */
	public static void close() {
		jedisPool.close();
	}

	/**
	 * 执行命令
	 * 
	 * @param command
	 * @param dbIndex
	 * @return
	 */
	public static <T> T execute(RedisCommandOperation<T> command,
			Integer dbIndex) {
		Jedis jedis = getInstance();
		try {
			if (jedis == null) {
				throw new RuntimeException("jedis is null");
			}
			if (dbIndex != null) {
				jedis.select(dbIndex);
			}
			return command.invoke(jedis);
		} catch (JedisConnectionException e) {
			logger.error("JedisConnectionException ERROR:{}", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			release(jedis);
		}
		return null;
	}

	// 管道不保证批量操作的原子性，说明操作的中途会被其他的客户端操作插入
	// 而multi保证操作的原子性
	// 从此处可以得知 pipeline操作要比multi效率高
	// 那么在应用场景上，如果不是对同一个 key进行操作，可以使用pipeline进行大量操作
	public static <T> T execute(RedisPipelineOperation<T> command,
			Integer dbIndex) {
		Jedis jedis = getInstance();
		Pipeline pipe = null;
		try {
			if (jedis == null) {
				throw new RuntimeException("jedis is null");
			}
			if (dbIndex != null) {
				jedis.select(dbIndex);
			}
			pipe = jedis.pipelined();

			if (pipe == null) {
				throw new RuntimeException("pipeline is null");
			}
			return command.invoke(pipe);
		} catch (JedisConnectionException e) {
			logger.error("JedisConnectionException ERROR:{}", e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			release(jedis);
		}
		return null;
	}

	/**
	 * 执行redis命令的接口，可以用于扩展
	 * 
	 * @author Administrator
	 * 
	 * @param <T>
	 */
	public interface RedisCommandOperation<T> {
		T invoke(Jedis jedis);
	}

	public interface RedisPipelineOperation<T> {
		T invoke(Pipeline pipe);
	}

	/**
	 * 获取key
	 * 
	 * @param key
	 * @return
	 */
	public static String get(final String key) {
		RedisCommandOperation<String> command = new RedisCommandOperation<String>() {
			@Override
			public String invoke(Jedis jedis) {
				return jedis.get(key);
			}
		};
		return execute(command, null);
	}

	/**
	 * 设置 key
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String set(final String key, final String value) {
		return execute(new RedisCommandOperation<String>() {
			@Override
			public String invoke(Jedis jedis) {
				return jedis.set(key, value);
			}
		}, null);
	}

	/**
	 * 使用 setnx 命令，如果已经存在，则返回0 表示插入失败，如果不存在，则set操作并返回 1
	 * 
	 * @param key
	 * @param value
	 * @return 设置成功，返回1 设置失败，返回0
	 */
	public static Boolean setnx(final String key, final String value) {
		return execute(new RedisCommandOperation<Boolean>() {
			@Override
			public Boolean invoke(Jedis jedis) {
				return jedis.setnx(key, value) == 1L;

			}
		}, null);
	}

	/**
	 * incr 命令，递增
	 * 
	 * @param key
	 *            键
	 * @param seconds
	 *            超时时间，单位 s
	 * @return 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。
	 */
	public static Long incr(final String key, final Integer seconds) {
		return execute(new RedisCommandOperation<Long>() {
			@Override
			public Long invoke(Jedis jedis) {
				if (seconds == null) {
					return jedis.incr(key);
				} else {
					// 开启事务
					Transaction transaction = jedis.multi();
					transaction.incr(key);
					transaction.expire(key, seconds);
					// 执行事务获取结果
					List<Object> result = transaction.exec();
					// 注意：redis返回的是基本数据类型，如果此处是Long，则会抛出异常
					return (long) result.get(0);
				}
			}
		}, null);
	}

	/**
	 * 删除key操作
	 * 
	 * @param keys
	 * @return
	 */
	public static Long del(final String... keys) {
		return execute(new RedisCommandOperation<Long>() {
			@Override
			public Long invoke(Jedis jedis) {
				if (keys == null || keys.length == 0) {
					return -1L;
				}
				Long del = jedis.del(keys);

				return del;
			}
		}, null);
	}

	/**
	 * 批量插入操作
	 * 
	 * @param keyValue
	 * @return
	 */
	public static Boolean msetByPipe(final Map<String, String> keyValue) {
		return execute(new RedisPipelineOperation<Boolean>() {
			@Override
			public Boolean invoke(Pipeline pipe) {
				if (keyValue != null && !keyValue.isEmpty()) {
					for (Entry<String, String> item : keyValue.entrySet()) {
						pipe.set(item.getKey(), item.getValue());
					}
					// 批量插入操作，有相同的key则被覆盖
					pipe.sync();
				}
				return true;
			}
		}, null);
	}

	/**
	 * 获取当前redis系统的时间
	 * 
	 * @return
	 */
	public static Long getTime() {
		return execute(new RedisCommandOperation<Long>() {
			@Override
			public Long invoke(Jedis jedis) {
				List<String> time = jedis.time();
				StringBuilder sb = new StringBuilder(time.get(0)).append(time
						.get(1));
				// 返回的是微秒，除1000返回ms数
				return Long.valueOf(sb.toString()) / 1000;
			}
		}, null);
	}

	/**
	 * 非Spring初始化Redis连接池
	 */
	// static{
	//
	// try {
	// Properties prop = new Properties();
	// InputStream is = ClassLoader
	// .getSystemResourceAsStream("redis.properties");
	// prop.load(is);
	//
	// String hostName = prop.getProperty("redis.host");
	// String masterName = prop.getProperty("redis.master.name");
	// String password = prop.getProperty("redis.password");
	// Integer dbIndex = Integer.parseInt(prop
	// .getProperty("redis.dbIndex"));
	// Integer connTimeout = Integer.parseInt(prop
	// .getProperty("redis.connection.timeout"));
	// Integer maxTotal = Integer.parseInt(prop
	// .getProperty("redis.pool.maxTotal"));
	// Integer minIdle = Integer.parseInt(prop
	// .getProperty("redis.pool.minIdle"));
	// Integer maxIdle = Integer.parseInt(prop
	// .getProperty("redis.pool.maxIdle"));
	// Long maxWaitMillis = Long.parseLong(prop
	// .getProperty("redis.pool.maxWaitMillis"));
	// Boolean blockWhenExhausted = Boolean.valueOf(prop
	// .getProperty("redis.pool.blockWhenExhausted"));
	// Boolean testOnBorrow = Boolean.valueOf(prop
	// .getProperty("redis.pool.testOnBorrow"));
	// Boolean testOnReturn = Boolean.valueOf(prop
	// .getProperty("redis.pool.testOnReturn"));
	// Boolean testWhileIdle = Boolean.valueOf(prop
	// .getProperty("redis.pool.testWhileIdle"));
	// Long minEvictableIdleTimeMillis = Long.parseLong(prop
	// .getProperty("redis.pool.minEvictableIdleTimeMillis"));
	// Long timeBetweenEvictionRunsMillis = Long.parseLong(prop
	// .getProperty("redis.pool.timeBetweenEvictionRunsMillis"));
	// Integer numTestsPerEvictionRun = Integer.parseInt(prop
	// .getProperty("redis.pool.numTestsPerEvictionRun"));
	// Long softMinEvictableIdleTimeMillis = Long.parseLong(prop
	// .getProperty("redis.pool.softMinEvictableIdleTimeMillis"));
	// Boolean lifo = Boolean.valueOf(prop.getProperty("redis.pool.lifo"));
	//
	// JedisPoolConfig config = new JedisPoolConfig();
	// config.setMaxTotal(maxTotal);
	// config.setMinIdle(minIdle);
	// config.setMaxIdle(maxIdle);
	// config.setMaxWaitMillis(maxWaitMillis);
	// config.setBlockWhenExhausted(blockWhenExhausted);
	// config.setTestOnBorrow(testOnBorrow);
	// config.setTestOnReturn(testOnReturn);
	// config.setTestWhileIdle(testWhileIdle);
	// config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	// config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	// config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	// config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
	// config.setLifo(lifo);
	//
	// Set<String> sentinels = new HashSet<String>();
	// sentinels.addAll(Arrays.asList(hostName.split(";")));
	// jedisPool = new JedisSentinelPool(masterName, sentinels, config,
	// connTimeout, password, dbIndex);
	// } catch (Exception e) {
	// logger.error("{}", e);
	// }
	// }
}
