package com.stt.curator.demo04_distributed;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Test04_DistributedDoubleBarrier {

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
                        // 创建 barrier 对象,注意最后一个参数表示要同步的分布式应用的个数
                        DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(
                                cf, barrierPath, 10);

                        System.out.println(Thread.currentThread().getName()
                                + "---prepared--");
                        Thread.sleep(1000);
                        // 在此处阻塞
                        barrier.enter();

                        // 满足条件后，10个应用都执行到enter后，继续执行
                        System.out.println(Thread.currentThread().getName()
                                + "---run--");
                        Thread.sleep(1000);

                        // 到达此处时继续阻塞
                        barrier.leave();

                        // 10个应用都到达了leave() 后，继续执行
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
