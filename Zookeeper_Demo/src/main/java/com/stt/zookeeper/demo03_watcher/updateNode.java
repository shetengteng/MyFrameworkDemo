package com.stt.zookeeper.demo03_watcher;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class updateNode {

    public static void main(String[] args) {
        String connectStr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        try {
            String parentPath = "/watcherTest";
            ZooKeeper zk = new ZooKeeper(connectStr, 2000, null);
            zk.setData(parentPath, "newValue".getBytes(), -1);
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
