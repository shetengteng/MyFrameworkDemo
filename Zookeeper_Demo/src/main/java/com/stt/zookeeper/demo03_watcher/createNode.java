package com.stt.zookeeper.demo03_watcher;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class createNode {

    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";

    public static void main(String[] args) {
        try {
            String parentPath = "/watcherTest";
            ZooKeeper zk = new ZooKeeper(connectStr, 2000, null);
            String result = zk.create(parentPath, "createNodeTest".getBytes(),
                    Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(result);
        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
