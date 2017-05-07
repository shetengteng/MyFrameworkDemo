package com.stt.zookeeper.demo02_watcher;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class createNode {

    public static void main(String[] args) {
        String connectStr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        try {
            String parentPath = "/watcherTest";
            String childrenPath = "/watcherTest/test01";
            ZooKeeper zk = new ZooKeeper(connectStr, 2000, null);

            String result = zk.create(childrenPath,
                    "createNodeTest".getBytes(), Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT);

            System.out.println(result);

        } catch (IOException | KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
