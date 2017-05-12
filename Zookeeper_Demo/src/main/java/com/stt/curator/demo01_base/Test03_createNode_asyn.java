package com.stt.curator.demo01_base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Test03_createNode_asyn {

    public static void main(String[] args) {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        String path = "/curator/test2";

        // 设置重连策略,初始时间为1s,重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        // 使用静态工厂类方法创建，可以使用链式编程风格，注意在最后使用build方法生成
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(connectAddr).connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout).retryPolicy(retryPolicy)
                .build();

        try {
            // 开启连接
            cf.start();

            // 回调函数
            BackgroundCallback callback = new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client,
                        CuratorEvent event) throws Exception {
                    // 获取上下文入参
                    System.out.println("入参：" + event.getContext());
                    System.out.println("事件类型：" + event.getType());
                    System.out.println("返回码" + event.getResultCode());
                    System.out.println("线程名："
                            + Thread.currentThread().getName());
                }
            };
            // 线程池
            ExecutorService pool = Executors.newCachedThreadPool();

            // 创建节点&赋值
            // creatingParentsIfNeeded 表示要创建父节点
            String forPath = cf.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .inBackground(callback, "ctx", pool)
                    .forPath(path, "value01".getBytes());

            System.out.println("createNode:" + forPath);

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            cf.close();
        }
    }
}
