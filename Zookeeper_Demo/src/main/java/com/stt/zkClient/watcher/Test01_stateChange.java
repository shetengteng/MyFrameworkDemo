package com.stt.zkClient.watcher;

import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.zookeeper.Watcher.Event.KeeperState;

public class Test01_stateChange {

    static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;
        ZkConnection connection = new ZkConnection(connectAddr, sessionTimeout);
        ZkClient zkClient = new ZkClient(connection, connectTimeout);
        System.out.println(zkClient.exists("/root"));

        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(KeeperState state) throws Exception {
                System.out.println(state);
                switch (state) {
                case SyncConnected:
                    System.out.println("已连接");
                    countDownLatch.countDown();
                    break;
                case AuthFailed:
                    System.out.println("认证失败");
                    break;
                case Disconnected:
                    System.out.println("失去链接");
                    break;
                case Expired:
                    System.out.println("会话过期");
                    break;
                default:
                    break;
                }
            }

            @Override
            public void handleNewSession() throws Exception {
                // 通常是由于session失效然后新的session被建立时触发。
                // 一般此时如果有创建临时节点，则需要重新创建
                System.out.println("获取到新的会话");
            }

            @Override
            public void handleSessionEstablishmentError(Throwable error)
                    throws Exception {
                // 获取异常信息
                System.out.println("ERROR:" + error.getMessage());
            }

        });

        countDownLatch.await();
        //
        // System.out.println(zkClient.exists("/root"));
        //
        // String path = "/zkClientTest";
        // zkClient.create(path, "test", CreateMode.PERSISTENT);
        // Thread.sleep(1000);
        //
        // zkClient.deleteRecursive(path);
        // Thread.sleep(1000);
    }
}
