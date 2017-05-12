package com.stt.curator.demo04_distributed;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Test01_InterProcessMutex {

    public static void main(String[] args) {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        final String path = "/curator/InterProcessMutex";

        // 设置重连策略,初始时间为1s,重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        final CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(connectAddr).connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout).retryPolicy(retryPolicy)
                .build();

        try {
            // 开启连接
            cf.start();
            // 获取分布式锁
            final InterProcessMutex lock = new InterProcessMutex(cf, path);
            final CountDownLatch countdown = new CountDownLatch(1);

            for (int i = 0; i < 10; i++) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 线程集体在此处睡眠
                            countdown.await();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        while (true) {
                            try {
                                // 获取分布式锁
                                lock.acquire();
                                byte[] value = cf.getData().forPath(path);
                                String result = new String(value);
                                System.out.println(Thread.currentThread()
                                        .getName() + ":" + result);
                                if ("1".equals(result)) {
                                    cf.setData().forPath(path, "0".getBytes());
                                } else {
                                    cf.setData().forPath(path, "1".getBytes());
                                }

                                Thread.sleep((long) (Math.random() * 100));
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (lock != null) {
                                    try {
                                        // 释放锁
                                        lock.release();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }, "thread" + i).start();
            }
            // 让10个线程一起执行
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
