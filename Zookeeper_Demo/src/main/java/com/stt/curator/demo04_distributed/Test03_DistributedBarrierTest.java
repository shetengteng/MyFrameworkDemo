package com.stt.curator.demo04_distributed;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Test03_DistributedBarrierTest {

    public static void main(String[] args) throws InterruptedException {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        final String barrierPath = "/curator/DistributedBarrier";

        final CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(connectAddr).connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        // 开启连接
        cf.start();
        // 创建 barrier 对象
        final DistributedBarrier barrier = new DistributedBarrier(cf,
                barrierPath);
        try {
            // 创建栅栏的path节点
            barrier.setBarrier();
            // 让应用一起启动，释放栅栏
            barrier.removeBarrier();
            System.out.println("-------------");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cf != null) {
                cf.close();
            }
        }
    }
}
