package com.stt.zkClient.base;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;

public class Test02_createNode {

    public static void main(String[] args) {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 2000;
        ZkConnection connection = new ZkConnection(connectAddr, sessionTimeout);
        ZkClient zkClient = new ZkClient(connection, connectTimeout);

        String path = "/zkClientTest3";

        // 创建节点
        zkClient.create(path, "test", CreateMode.PERSISTENT);

        // 创建持久节点
        String path2 = "/creatNode/test01/test";
        zkClient.createPersistent(path2, true);

        // 注意：创建持久节点可以递归创建，但是值是null
        // 需要单独赋值
        zkClient.writeData(path2, "createNode");

        // 判断节点是否存在
        boolean exists = zkClient.exists(path2);
        if (exists) {
            // 获取节点
            // 设置true,表示如果节点不存在，则返回null，不抛出异常
            Object data = zkClient.readData(path2, true);
            System.out.println("result:" + data);

            List<String> children = zkClient.getChildren(path2);
            for (String subPath : children) {
                System.out.println(subPath);
            }
        }

    }
}
