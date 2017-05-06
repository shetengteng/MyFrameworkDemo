package com.stt.zookeeper.demo01_base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

public class Test01_connection {

    /**
     * 在建立zk连接的时候，由于是异步的，当main线程结束了，那么建立zk的线程就不存在
     * 
     * 因此需要使用睡眠，或者countdownlatch的方式进行协调操作
     */
    public static void main(String[] args) {
        // 建立countDownLatch ,同时 只有一个线程进行countDown操作
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        ZooKeeper zk = null;

        try {

            // ZK的集群的地址，这里是伪集群方式
            String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
            // session的超时时间 ms
            int sessionTimeout = 2000;

            // 事件处理通知器
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 获取事件的处理状态
                    KeeperState state = event.getState();
                    // 表示同步连接状态
                    if (KeeperState.SyncConnected == state) {
                        EventType type = event.getType();
                        if (EventType.None == type) {
                            // 当zk建立完毕连接之后，才可以继续主线程
                            countDownLatch.countDown();
                            System.out.println("----connected----");
                        }
                    }
                }
            };
            // 创建zk的客户端
            zk = new ZooKeeper(connectAddr, sessionTimeout, watcher);

            // 此处进行wait操作
            countDownLatch.await();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (zk != null) {
                try {
                    zk.close();
                    System.out.println("---zk close----");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
