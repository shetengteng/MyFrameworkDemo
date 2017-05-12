package com.stt.curator.demo01_base;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.Code;

public class Test05_deleteNode_asyn {

    public static void main(String[] args) {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        String path = "/curator/test";

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

            BackgroundCallback callback = new BackgroundCallback() {
                @Override
                public void processResult(CuratorFramework client,
                        CuratorEvent event) throws Exception {
                    // 获取上下文入参
                    System.out.println("入参：" + event.getContext());
                    System.out.println("事件类型：" + event.getType());
                    Code code = KeeperException.Code.get(event.getResultCode());
                    System.out.println("返回码：" + code);
                    System.out.println("线程名："
                            + Thread.currentThread().getName());
                }
            };
            // 线程池
            ExecutorService pool = Executors.newCachedThreadPool();
            // 删除节点
            cf.delete()
            // 保证机制，出错后后台删除 直到删除成功
                    .guaranteed()
                    // 删除当前节点下的所有节点，再删除自身
                    .deletingChildrenIfNeeded().withVersion(-1)
                    // 异步回调函数
                    .inBackground(callback, pool).forPath(path);

            Thread.sleep(1000);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            cf.close();
        }
    }
}
