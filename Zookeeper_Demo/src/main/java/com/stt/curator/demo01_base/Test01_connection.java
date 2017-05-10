package com.stt.curator.demo01_base;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Test01_connection {

    public static void main(String[] args) {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        // 设置重连策略,初始时间为1s,重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        // 使用静态工厂类方法创建，可以使用链式编程风格
        // 注意在最后使用build方法生成
        Builder builder = CuratorFrameworkFactory.builder();
        // 设置连接地址
        builder.connectString(connectAddr);
        builder.connectionTimeoutMs(connectTimeout);
        builder.sessionTimeoutMs(sessionTimeout);
        // 设置重连策略
        builder.retryPolicy(retryPolicy);
        // 生成客户端
        CuratorFramework cf = builder.build();

        // 开启连接
        cf.start();
    }
}
