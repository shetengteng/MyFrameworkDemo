package com.stt.zkClient.watcher;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.CreateMode;

public class Test03_childChange {

    public static void main(String[] args) throws InterruptedException {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 2000;
        ZkConnection connection = new ZkConnection(connectAddr, sessionTimeout);
        final ZkClient zkClient = new ZkClient(connection, connectTimeout);

        String path = "/zkClientTest";
        // 查看子节点的变化
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath,
                    List<String> currentChilds) throws Exception {
                System.out.println("-----start----");
                System.out.println("父节点：" + parentPath);
                if (currentChilds != null && !currentChilds.isEmpty()) {
                    System.out.println("直接子节点列表：");
                    for (String path : currentChilds) {
                        System.out.println("---" + path);
                    }
                } else {
                    System.out.println("直接子节点列表为空");
                    boolean exists = zkClient.exists(parentPath);
                    if (!exists) {
                        System.out.println(parentPath + "已删除");
                    } else {
                        System.out.println(parentPath + "已创建");
                    }
                }
                System.out.println("-----end----");
            }
        });

        // 创建节点
        zkClient.create(path, "test", CreateMode.PERSISTENT);
        Thread.sleep(1000);

        // 创建子节点,注意创建父节点要为true
        // zkClient.createPersistent(path + "/s1/r1", true);
        Thread.sleep(1000);

        // zkClient.createPersistent(path + "/s2/r2", true);
        Thread.sleep(1000);

        zkClient.createPersistent(path + "/s3", true);
        Thread.sleep(1000);

        // 删除节点
        // zkClient.deleteRecursive(path);

        zkClient.delete(path + "/s3");
        Thread.sleep(1000);
        zkClient.delete(path);

        Thread.sleep(10000);
    }
}
