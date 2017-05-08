package com.stt.zookeeper.demo03_watcher;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class deleteChildrenNode {

    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";

    public static void main(String[] args) {
        // String connectStr =
        // "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        try {
            String childrenPath = "/watcherTest/test01";
            ZooKeeper zk = new ZooKeeper(connectStr, 2000, null);
            zk.delete(childrenPath, -1);
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
