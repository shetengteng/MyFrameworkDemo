package com.stt.zookeeper.demo05_cluster;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Test01_setNode {

    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
    private static String configPath = "/configRoot";

    public static void main(String[] args) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(connectStr, 2000, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 表示同步连接状态
                    if (KeeperState.SyncConnected == event.getState()) {
                        if (EventType.None == event.getType()) {
                            // 当zk建立完毕连接之后，才可以继续主线程
                            countDownLatch.countDown();
                            System.out.println("----connected----");
                        }
                    }
                }
            });
            countDownLatch.await();

            // 创建节点信息
            zk.create(configPath, "configRoot".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            Thread.sleep(1000);

            zk.create(configPath + "/s1", "s1".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            Thread.sleep(1000);

            zk.create(configPath + "/s2", "s2".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);
            Thread.sleep(1000);

            zk.setData(configPath, "newValue01".getBytes(), -1);
            Thread.sleep(1000);

            zk.setData(configPath + "/s2", "new s2".getBytes(), -1);
            Thread.sleep(1000);

            zk.create(configPath + "/s2/r1", "r1".getBytes(),
                    Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            Thread.sleep(1000);

            zk.setData(configPath + "/s2/r1", "new r1".getBytes(), -1);
            Thread.sleep(1000);

            zk.delete(configPath + "/s2/r1", -1);
            Thread.sleep(1000);

            zk.delete(configPath + "/s2", -1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (zk != null) {
                try {
                    zk.close();
                } catch (InterruptedException e) {
                }
            }
        }

    }
}
