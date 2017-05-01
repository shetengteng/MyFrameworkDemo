package com.stt.test;

import org.junit.Test;

import com.stt.utils.RedisDistributedLockUtil;
import com.stt.utils.RedisDistributedLockUtil.WorkTask;

public class DistributedLockTest extends BaseTest {

	// 注意：在测试多线程的时候，不建议使用junit，因为只要子线程进行阻塞睡眠，就会退出执行
	// 如果使用main函数进行测试则没有这个问题
	// 如果要使用junit测试多线程，可以让主线程睡眠一段时间
	@Test
	public void test02() {

		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("ddd");
					for (;;) {
						System.out.println(Thread.currentThread().getName()
								+ "---");
					}
				}
			}).start();
		}

	}

	static int test = 0;

	@Test
	public void test01() {

		// 多个线程去获取锁
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					System.out.println("start--"
							+ Thread.currentThread().getName());
					for (;;) {
						String result = RedisDistributedLockUtil.Operation(
								"stt", new WorkTask<String>() {
									@Override
									public String invoke() {
										try {
											Thread.sleep((long) (Math.random() * 500));
										} catch (Exception e) {
										}
										if (test == 0) {
											test++;
										} else if (test == 1) {
											test--;
										} else {
											return "###################" + test;
										}
										return Thread.currentThread().getName()
												+ "--- " + test;
									}
								});
						// 打印结果
						System.out.println(result);
					}
				}
			}, "T--" + i).start();
		}

		for (;;) {
			try {
				Thread.sleep((long) (50000000));
			} catch (Exception e) {
			}
		}

	}
}
