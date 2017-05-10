package com.stt.curator.demo01_base;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Test03_updateNode {

    public static void main(String[] args) {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        String path = "/curator/test";

        // 设置重连策略,初始时间为1s,重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        // 使用静态工厂类方法创建，可以使用链式编程风格
        // 注意在最后使用build方法生成
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(connectAddr).connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout).retryPolicy(retryPolicy)
                .build();

        try {
            // 开启连接
            cf.start();

            // 创建节点&赋值
            // creatingParentsIfNeeded 表示要创建父节点
            String forPath = cf.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, "value01".getBytes());

            System.out.println("createNode:" + forPath);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            cf.close();
        }

    }
}
