package com.stt.zookeeper.demo01_base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class Test11_exists_Syn {

    /**
     * 在建立zk连接的时候，由于是异步的，当main线程结束了，那么建立zk的线程就不存在
     * 
     * 因此需要使用睡眠，或者countdownlatch的方式进行协调操作
     */
    private static Boolean isConnected = false;

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
                            System.out.println("----connected----");
                            isConnected = true;
                            countDownLatch.countDown();
                        }
                    }
                }
            };
            // 创建zk的客户端
            zk = new ZooKeeper(connectAddr, sessionTimeout, watcher);
            // 此处进行wait操作
            countDownLatch.await();
            // 创建节点
            if (isConnected) {

                String path = "/myTestRoot33";
                Stat stat = zk.exists(path, false);
                // 返回的是节点的详细信息
                System.out.println(stat);
                if (stat != null) {
                    System.out.println("节点存在");
                } else {
                    System.out.println("节点不存在");
                }
            } else {
                throw new RuntimeException("----connect failed----");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
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
