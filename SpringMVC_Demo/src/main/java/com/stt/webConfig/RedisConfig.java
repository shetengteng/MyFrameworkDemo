package com.stt.webConfig;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import com.stt.utils.RedisDistributedLockUtil;
import com.stt.utils.RedisUtil;

/**
 * 缓存配置redis
 * 
 * @author Administrator
 * 
 */
@Configuration
@ComponentScan(basePackageClasses = { RedisUtil.class,
		RedisDistributedLockUtil.class })
@PropertySource("classpath:config/redisConfig.properties")
public class RedisConfig {
	private final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

	@Value("${redis.hosts}")
	private String hostName;

	@Value("${redis.host}")
	private String host;

	@Value("${redis.port}")
	private int port;
	@Value("${redis.master.name}")
	private String master;
	@Value("${redis.password}")
	private String password;
	@Value("${redis.dbIndex}")
	private int dbIndex;
	@Value("${redis.connection.timeout}")
	private int connTimeout;

	@Value("${redis.pool.maxTotal}")
	private int maxTotal;

	@Value("${redis.pool.minIdle}")
	private int minIdle;

	/**
	 * 控制一个pool最多有多少个状态为idle(空闲)的jedis实例
	 */
	@Value("${redis.pool.maxIdle}")
	private int maxIdle;

	@Value("${redis.pool.maxWaitMillis}")
	private long maxWaitMillis;

	@Value("${redis.pool.blockWhenExhausted}")
	private boolean blockWhenExhausted;

	/**
	 * 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
	 */
	@Value("${redis.pool.testOnBorrow}")
	private boolean testOnBorrow;

	/**
	 * 在return给pool时，是否提前进行validate操作
	 */
	@Value("${redis.pool.testOnReturn}")
	private boolean testOnReturn;

	/**
	 * 如果为true，表示有一个idle object evitor线程对idle
	 * object进行扫描，如果validate失败，此object会被从pool中drop掉
	 * ；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
	 */
	@Value("${redis.pool.testWhileIdle}")
	private boolean testWhileIdle;

	/**
	 * 表示idle object evitor两次扫描之间要sleep的毫秒数
	 */
	@Value("${redis.pool.timeBetweenEvictionRunsMillis}")
	private long timeBetweenEvictionRunsMillis;

	/**
	 * 表示idle object evitor每次扫描的最多的对象数
	 */
	@Value("${redis.pool.numTestsPerEvictionRun}")
	private int numTestsPerEvictionRun;

	/**
	 * 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
	 * evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
	 */
	@Value("${redis.pool.minEvictableIdleTimeMillis}")
	private long minEvictableIdleTimeMillis;

	/**
	 * 在minEvictableIdleTimeMillis基础上，加入了至少minIdle个对象已经在pool里面了。如果为-1，
	 * evicted不会根据idle time驱逐任何对象。如果minEvictableIdleTimeMillis>0，则此项设置无意义，
	 * 且只有在timeBetweenEvictionRunsMillis大于0时才有意义
	 */
	@Value("${redis.pool.softMinEvictableIdleTimeMillis}")
	private long softMinEvictableIdleTimeMillis;

	/**
	 * borrowObject返回对象时，是采用DEFAULT_LIFO（last in first
	 * out，即类似cache的最频繁使用队列），如果为False，则表示FIFO队列
	 */
	@Value("${redis.pool.lifo}")
	private boolean lifo;

	@Bean
	public JedisPoolConfig jedisPoolConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		//
		config.setMaxTotal(maxTotal);
		config.setMinIdle(minIdle);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setBlockWhenExhausted(blockWhenExhausted);
		config.setTestOnBorrow(testOnBorrow);
		config.setTestOnReturn(testOnReturn);
		config.setTestWhileIdle(testWhileIdle);
		config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		config.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		config.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		config.setSoftMinEvictableIdleTimeMillis(softMinEvictableIdleTimeMillis);
		config.setLifo(lifo);
		return config;
	}

	/**
	 * 返回JedisPool,含有主从模式的
	 * 
	 * @return
	 */
	// @Bean
	public JedisSentinelPool jedisPool() {
		Set<String> sentinels = new HashSet<String>();
		// 配置多个redis的host(这里的host格式是IP:port)
		sentinels.addAll(Arrays.asList(hostName.split(";")));
		JedisSentinelPool jedisSentinelPool = new JedisSentinelPool(master,
				sentinels, jedisPoolConfig(), connTimeout, password, dbIndex);
		return jedisSentinelPool;
	}

	@Bean
	public JedisPool jedisClientPool() {

		// JedisPoolConfig config = new JedisPoolConfig();
		// config.setMaxTotal(20);// 最大连接数
		// config.setMaxIdle(8);
		// // 设置最小空闲数
		// config.setMinIdle(1);
		// // 首次会有链接超时的情况
		// config.setMaxWaitMillis(60 * 1000);// 30s
		// config.setTestOnBorrow(true);
		// // Idle时进行连接扫描
		// config.setTestWhileIdle(true);
		// // 表示idle object evitor两次扫描之间要sleep的毫秒数
		// config.setTimeBetweenEvictionRunsMillis(30000);
		// // 表示idle object evitor每次扫描的最多的对象数
		// config.setNumTestsPerEvictionRun(-1);
		// // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object
		// // evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
		// config.setMinEvictableIdleTimeMillis(60000);
		JedisPool jedisPool = new JedisPool(jedisPoolConfig(), host, port);
		return jedisPool;
	}
}
