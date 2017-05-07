package com.stt.zookeeper.demo01_base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Test02_createNode_Syn {

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
            // String connectAddr =
            // "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
            String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
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
                // 只有建立完毕连接之后才能进行创建节点的操作
                // 创建的根节点
                String path = "/myTestRoot03";
                // 创建的根节点的数据
                byte[] data = "createNodeTest".getBytes();
                // 创建节点，如果节点已经存在，则会抛出异常
                zk.create(path, data, Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);
                // 创建节点的父路径如果不存在，则会抛出异常
                String returnStr = zk.create("/myTestRoot02/tem",
                        "test".getBytes(), Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT);

                System.out.println(returnStr);

                System.out.println("---create node---");
            } else {
                throw new RuntimeException("----connect failed----");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
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
