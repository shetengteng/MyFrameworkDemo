package com.stt.curator.demo04_distributed;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Test03_DistributedBarrier {

    public static void main(String[] args) throws InterruptedException {

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
                        String barrierPath = "/curator/DistributedBarrier";
                        CuratorFramework cf = CuratorFrameworkFactory
                                .builder()
                                .connectString(connectAddr)
                                .connectionTimeoutMs(5000)
                                .sessionTimeoutMs(2000)
                                .retryPolicy(
                                        new ExponentialBackoffRetry(1000, 10))
                                .build();
                        // 开启连接
                        cf.start();
                        // 创建 barrier 对象
                        DistributedBarrier barrier = new DistributedBarrier(cf,
                                barrierPath);
                        // 创建栅栏的path节点
                        barrier.setBarrier();
                        System.out.println(Thread.currentThread().getName()
                                + "---run--");
                        // 应用在此处阻塞
                        barrier.waitOnBarrier();

                        System.out.println(Thread.currentThread().getName()
                                + "---finish--");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, "Thread:" + i).start();
        }
        for (;;) {
            Thread.sleep(1000);
        }
    }
}
