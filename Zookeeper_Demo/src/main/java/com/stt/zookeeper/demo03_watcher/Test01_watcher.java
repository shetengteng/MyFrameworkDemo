package com.stt.zookeeper.demo03_watcher;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 该示例模拟多个客户端对同一个node的操作
 * 
 * @author Administrator
 * 
 */
public class Test01_watcher {

    // 用于建立连接，异步变同步使用
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);
    private static ZooKeeper zk = null;
    private static String parentPath = "/watcherTest";
    private static String childrenPath = "/watcherTest/test01";
    // 定义原子变量，用于记录次数
    private static AtomicInteger seq = new AtomicInteger();
    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
    private static Boolean isConnected = false;

    public static void main(String[] args) {
        int sessionTimeout = 2000;

        try {
            // 创建了一个watcher对象
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    System.out.println("-----进入了 process 的线程-----"
                            + seq.incrementAndGet());
                    System.out.println("连接的状态：" + event.getState());
                    System.out.println("事件的类型：" + event.getType());
                    System.out.println("事件触发的路径：" + event.getPath());
                    try {
                        watchProcess(event, zk, parentPath, childrenPath);
                    } catch (KeeperException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            zk = new ZooKeeper(connectStr, sessionTimeout, watcher);
            countDownLatch.await();

            for (;;) {
                Thread.sleep(10000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (zk != null) {
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 对watch的事件进行处理
    private static void watchProcess(WatchedEvent event, ZooKeeper zk,
            String... path) throws KeeperException, InterruptedException {
        // 判断是建立了连接
        if (KeeperState.SyncConnected == event.getState()) {
            switch (event.getType()) {
            case None:
                countDownLatch.countDown();
                System.out.println("---建立连接---");
                break;
            case NodeCreated:
                System.out.println(">>创建节点触发");
                break;
            case NodeDataChanged:
                System.out.println(">>节点数据改变触发");
                break;
            case NodeChildrenChanged:
                System.out.println(">>节点的子节点发生改变触发");
                break;
            case NodeDeleted:
                System.out.println(">>节点删除触发");
                break;
            default:
                break;
            }
            // 每次触发自动启用true，表示需要再次watch
            // 注意：这里的exists操作等价于在watch中注册了要监听触发的path
            // 由于watch监听是一次性的，因此需要反复的设置为true
            // 这里使用循环，表示可以同时watch多个path
            for (String item : path) {
                if (item.equals(event.getPath()) || event.getPath() == null) {
                    Stat stat = zk.exists(item, true);
                    // 要获取子节点的删除修改，需要使用getChildren,同时要设置为true
                    if (stat != null) {
                        zk.getChildren(item, true);
                    }
                }
            }
        } else if (KeeperState.AuthFailed == event.getState()) {
            System.out.println("认证失败");
        } else if (KeeperState.Expired == event.getState()) {
            System.out.println("会话超时");
        } else if (KeeperState.Disconnected == event.getState()) {
            System.out.println("与ZK断开连接");
        }
    }
}
