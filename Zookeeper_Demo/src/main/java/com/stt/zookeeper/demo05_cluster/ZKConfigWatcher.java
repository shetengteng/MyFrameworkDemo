package com.stt.zookeeper.demo05_cluster;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

// 分布式应用中用于监听配置变化的watcher
public class ZKConfigWatcher implements Watcher {

    private CountDownLatch countDownLatch = new CountDownLatch(1);
    private ZooKeeper zk = null;
    private String configPath;

    public ZKConfigWatcher(String connectAddr, String configPath) {
        try {
            this.configPath = configPath;
            zk = new ZooKeeper(connectAddr, 2000, this);
            countDownLatch.await();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void process(WatchedEvent event) {
        System.out.println("连接的状态：" + event.getState());
        System.out.println("事件的类型：" + event.getType());
        System.out.println("事件触发的路径：" + event.getPath());
        String eventPath = event.getPath();
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
                // 如果要获取到子节点，那么需要存储已有的直接子节点，从中作出比较，判断哪些是新增，哪些是删除的
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
            // 监听所有configPath下的直接子节点的状态与更新
            try {
                String path = eventPath == null ? configPath : eventPath;
                if (null != zk.exists(path, true)) {
                    // 此处可以监听子节点的添加和删除操作，但是子节点的数据改变需要用exists来监听
                    List<String> pathList = zk.getChildren(path, true);
                    if (pathList != null && !pathList.isEmpty()) {
                        for (String p : pathList) {
                            // 添加监听
                            zk.exists(path + "/" + p, true);
                        }
                    }
                }
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

        } else if (KeeperState.AuthFailed == event.getState()) {
            System.out.println("认证失败");
        } else if (KeeperState.Expired == event.getState()) {
            System.out.println("连接超时");
        } else if (KeeperState.Disconnected == event.getState()) {
            System.out.println("断开链接");
        }
    }
}
