package com.stt.test;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import com.stt.utils.RedisUtil;
import com.stt.utils.RedisUtil.RedisCommandOperation;
import com.stt.utils.RedisUtil.RedisPipelineOperation;

/**
 * 在测试最后的时候要关闭链接池
 * 
 * @author Administrator
 * 
 */
public class RedisTest extends BaseTest {

	@Test
	public void testSet() {
		String result = RedisUtil.set("test01", "test01");
		System.out.println(result);
	}

	@Test
	public void testGet() {
		String result = RedisUtil.get("test09");
		System.out.println(result);
	}

	@Test
	public void testSetnx() throws InterruptedException {
		for (int i = 0; i < 220; i++) {
			try {
				Boolean result = RedisUtil.setnx("test02", "ss");
				System.out.println(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testIncr() {
		Long incr = RedisUtil.incr("test01", 2000);
		System.out.println(incr);
	}

	@Test
	public void testDel() {
		// RedisUtil.incr("test03", null);
		Long del = RedisUtil.del("test05");
		System.out.println(del);
	}

	@Test
	public void testDelM() {
		String[] sum = new String[10];
		for (int i = 0; i < 10; i++) {
			RedisUtil.set("c" + i, i + "");
			sum[i] = "c" + i;
		}
		System.out.println(RedisUtil.del(sum));
	}

	@Test
	public void testGetTime() {
		Long time = RedisUtil.getTime();
		System.out.println(time);

		System.out.println(System.currentTimeMillis());
		System.nanoTime();

		DateTime dataTime = new DateTime(time);
		String timeStr = dataTime.toString("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(timeStr);
	}

	// 管道不保证批量操作的原子性，说明操作的中途会被其他的客户端操作插入
	// 而multi保证操作的原子性
	// 从此处可以得知 pipeline操作要比multi效率高
	// 那么在应用场景上，如果不是对同一个 key进行操作，可以使用pipeline进行大量操作
	@Test
	public void testPipline() {
		RedisUtil.execute(new RedisCommandOperation<Boolean>() {
			@Override
			public Boolean invoke(Jedis jedis) {
				Pipeline pipeline = jedis.pipelined();
				Response<String> set = pipeline.set("pipeline01", "pipeline01");
				pipeline.sync();

				// List<Object> all = pipeline.syncAndReturnAll();

				return true;
			}
		}, null);

	}

	@Test
	public void testPipeline2() {
		Boolean result = RedisUtil.execute(
				new RedisPipelineOperation<Boolean>() {
					@Override
					public Boolean invoke(Pipeline pipe) {
						pipe.set("pipe001", "pipe002");
						System.out.println("---1---");
						return true;
					}
				}, null);
		System.out.println(result);
	}

	@Test
	public void testPipeline3() {
		RedisPipelineOperation<List<Object>> command = new RedisPipelineOperation<List<Object>>() {
			@Override
			public List<Object> invoke(Pipeline pipe) {
				pipe.get("pipe001");
				List<Object> syncAndReturnAll = pipe.syncAndReturnAll();
				return syncAndReturnAll;
			}
		};
		System.out.println(RedisUtil.execute(command, null));
	}

	@Test
	public void testPipeline4() {
		RedisPipelineOperation<List<Object>> command = new RedisPipelineOperation<List<Object>>() {
			@Override
			public List<Object> invoke(Pipeline pipe) {
				for (int i = 0; i < 10; i++) {
					pipe.set("pipe00" + i, i + "");
				}
				List<Object> syncAndReturnAll = pipe.syncAndReturnAll();
				return syncAndReturnAll;
			}
		};
		System.out.println(RedisUtil.execute(command, null));
	}

	@After
	public void destroy() {
		RedisUtil.close();
	}
}
