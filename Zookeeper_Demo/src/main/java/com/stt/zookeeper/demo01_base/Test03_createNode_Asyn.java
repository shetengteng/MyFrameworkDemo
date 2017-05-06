package com.stt.zookeeper.demo01_base;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

/**
 * 异步的方式创建节点
 * 
 * @author Administrator
 * 
 */
public class Test03_createNode_Asyn {

    /**
     * 在建立zk连接的时候，由于是异步的，当main线程结束了，那么建立zk的线程就不存在
     * 
     * 因此需要使用睡眠，或者countdownlatch的方式进行协调操作
     */
    private static Boolean isConnected = false;

    public static void main(String[] args) {
        // 建立countDownLatch ,同时 只有一个线程进行countDown操作
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zk = null;
        try {

            // ZK的集群的地址，这里是伪集群方式
            String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
            // session的超时时间 ms
            int sessionTimeout = 2000;

            // 事件处理通知器
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    // 获取事件的处理状态
                    KeeperState state = event.getState();
                    // 表示同步连接状态
                    if (KeeperState.SyncConnected == state) {
                        EventType type = event.getType();
                        if (EventType.None == type) {
                            // 当zk建立完毕连接之后，才可以继续主线程
                            System.out.println("----connected----");
                            isConnected = true;
                            countDownLatch.countDown();
                        }
                    }
                }
            };
            // 创建zk的客户端
            zk = new ZooKeeper(connectAddr, sessionTimeout, watcher);
            // 此处进行wait操作
            countDownLatch.await();

            if (isConnected) {
                String path = "/myTestRootAsyn"; // 创建的根节点
                byte[] data = "createNodeTestAysn".getBytes(); // 创建的根节点的数据

                // 异步创建节点的回调函数
                StringCallback cb = new StringCallback() {
                    @Override
                    public void processResult(int rc, String path, Object ctx,
                            String name) {
                        switch (rc) {
                        case 0:
                            System.out.println("操作成功");
                            break;
                        case -4:
                            System.out.println("端口连接");
                            break;
                        case -110:
                            System.out.println("指定节点已存在");
                            break;
                        case -112:
                            System.out.println("会话过期");
                            break;
                        default:
                            break;
                        }
                        System.out.println("传入要创建的path:" + path);
                        System.out.println("实际在服务器节点的名称name:" + name);
                        System.out.println("传入的上下文信息参数ctx:" + ctx);
                        if (path.equals(name) && rc == 0) {
                            System.out.println("创建成功");
                        }
                    }
                };
                // 异步创建创建节点没有同步返回值
                zk.create(path, data, Ids.OPEN_ACL_UNSAFE,
                        CreateMode.PERSISTENT, cb, "ctx");

                // 由于是异步创建，此处最好sleep一段时间，等待回调
                Thread.sleep(1000);

                System.out.println("---create node---");
            } else {
                throw new RuntimeException("----connect failed----");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (zk != null) {
                try {
                    zk.close();
                    System.out.println("---zk close----");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
