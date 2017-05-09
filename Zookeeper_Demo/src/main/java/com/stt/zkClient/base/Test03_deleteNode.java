package com.stt.zkClient.base;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

public class Test03_deleteNode {

    public static void main(String[] args) {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 2000;
        ZkConnection connection = new ZkConnection(connectAddr, sessionTimeout);
        ZkClient zkClient = new ZkClient(connection, connectTimeout);

        // 删除节点
        String path = "/zkClientTest3";
        boolean delete = zkClient.delete(path, -1);
        System.out.println(path + ": delete :" + delete);

        // 递归删除
        String path2 = "/creatNode/test01/test";
        boolean deleteRecursive = zkClient.deleteRecursive("/creatNode");
        System.out.println(path2 + ": delete :" + deleteRecursive);

    }
}
