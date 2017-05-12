package com.stt.curator.demo04_distributed;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

/**
 * 分布式应用下的原子类型的操作
 * 
 * @author Administrator
 * 
 */
public class Test02_DistributedAtomicInteger {

    public static void main(String[] args) {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        final String counterPath = "/curator/DistributedAtomicInteger";

        final CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(connectAddr).connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();

        try {
            // 开启连接
            cf.start();

            final CountDownLatch countdown = new CountDownLatch(1);
            for (int i = 0; i < 2; i++) {
                new Thread(new Runnable() {

                    // 设置原子类型，重试策略是 重试3次，每次重试间隔1s
                    DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(
                            cf, counterPath, new RetryNTimes(3, 1000));

                    @Override
                    public void run() {
                        try {
                            countdown.await();
                            for (int i = 0; i < 5; i++) {
                                // AtomicValue<Integer> result =
                                // atomicInteger.increment();
                                // 此处+1 操作等价
                                AtomicValue<Integer> result = atomicInteger.add(1);
                                System.out.println(result.succeeded());
                                System.out.println(Thread.currentThread()
                                        .getName()
                                        + ":"
                                        + result.postValue()
                                        + ":" + result.preValue());
                                Thread.sleep(1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, "Thread:" + i).start();
            }
            countdown.countDown();
            for (;;) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            cf.close();
        }
    }
}
