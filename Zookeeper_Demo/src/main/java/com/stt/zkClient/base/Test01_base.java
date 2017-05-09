package com.stt.zkClient.base;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;

public class Test01_base {

    public static void main(String[] args) {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 2000;
        ZkConnection connection = new ZkConnection(connectAddr, sessionTimeout);
        ZkClient zkClient = new ZkClient(connection, connectTimeout);

        // 创建节点
        String path = "/zkClientTest2";
        zkClient.create(path, "test", CreateMode.PERSISTENT);

        // 判断节点是否存在
        boolean exists = zkClient.exists(path);
        if (exists) {
            // 获取节点
            Object data = zkClient.readData(path);
            System.out.println("result:" + data);

            // 修改节点
            zkClient.writeData(path, "newValue", -1);

            // 删除节点
            boolean delete = zkClient.delete(path);
            System.out.println(delete);
        }
    }
}
