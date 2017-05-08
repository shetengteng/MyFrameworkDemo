package com.stt.zookeeper.demo02_watcher;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 模拟一个客户端对node的不同时间点的操作，watch的状态
 * 
 * @author Administrator
 * 
 */
public class Test01_watcher {

    // 用于建立连接，异步变同步使用
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ZooKeeper zk = null;
    private static String parentPath = "/watcherTest";
    private static String childrenPath = "/watcherTest/test01";

    // 定义原子变量，用于记录次数
    private static AtomicInteger seq = new AtomicInteger();
    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";

    // private static String connectStr =
    // "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";

    public static void main(String[] args) {
        int sessionTimeout = 2000;

        try {
            // 创建了一个watcher对象
            Watcher watcher = new Watcher() {
                @Override
                public void process(WatchedEvent event) {

                    System.out.println("-----进入了 process 的线程-----"
                            + seq.incrementAndGet());
                    System.out.println("连接的状态：" + event.getState());
                    System.out.println("事件的类型：" + event.getType());
                    String eventPath = event.getPath();
                    System.out.println("事件触发的路径：" + eventPath);

                    if (KeeperState.SyncConnected == event.getState()) {
                        switch (event.getType()) {
                        case None:
                            countDownLatch.countDown();
                            System.out.println("---建立连接---");
                            break;
                        case NodeCreated:
                            System.out.println(eventPath + ">>创建节点触发");
                            break;
                        case NodeDataChanged:
                            System.out.println(eventPath + ">>节点数据改变触发");
                            break;
                        case NodeChildrenChanged:
                            System.out.println(eventPath + ">>节点的子节点发生改变触发");
                            break;
                        case NodeDeleted:
                            System.out.println(eventPath + ">>节点删除触发");
                            break;
                        default:
                            break;
                        }
                    }
                }
            };

            zk = new ZooKeeper(connectStr, sessionTimeout, watcher);
            countDownLatch.await();

            // 测试创建节点,触发watch
            createNode(parentPath, zk);
            Thread.sleep(1000);
            // 读取节点
            readNode(parentPath, zk);
            Thread.sleep(1000);
            // 设置节点
            setNode(parentPath, zk);
            Thread.sleep(1000);
            // 获取子节点
            getChildrenNode(parentPath, zk);
            // 创建子节点
            createNode(childrenPath, zk);
            Thread.sleep(1000);
            Thread.sleep(1000);
            // 设置子节点
            setNode(childrenPath, zk);
            Thread.sleep(1000);

            System.out.println("*************");

            // 删除节点
            deleteNode(childrenPath, zk);
            Thread.sleep(1000);
            deleteNode(parentPath, zk);
            Thread.sleep(1000);

            for (;;) {
                Thread.sleep(10000);
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        } finally {
            if (zk != null) {
                try {
                    zk.close();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public static void createNode(String path, ZooKeeper zk)
            throws KeeperException, InterruptedException {
        // 如果是创建一个节点，需要使用exist先注册watch事件，注意，watch是一次性的，因此
        // 如果要每次都能被watch的话，需要每次都要exists一下
        Stat stat = zk.exists(path, true);
        if (stat == null) {
            String result = zk.create(path, "createNodeTest".getBytes(),
                    Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("createNode path:" + path + " result:" + result);
        }
    }

    public static void readNode(String path, ZooKeeper zk)
            throws KeeperException, InterruptedException {
        // 获取数据，设置watch为true
        // 等价于使用exists，getData的watch设置为false
        byte[] data = zk.getData(path, true, null);
        System.out.println("readNode path:" + path + " value:"
                + new String(data));
    }

    public static void setNode(String path, ZooKeeper zk)
            throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, true);
        if (stat != null) {
            zk.setData(path, "newValue".getBytes(), -1);
            System.out.println("setNode path:" + path + " value:newValue");
        } else {
            System.out.println(path + "not exists");
        }
    }

    public static void deleteNode(String path, ZooKeeper zk)
            throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, true);
        if (stat != null) {
            zk.delete(path, -1);
            System.out.println("deleteNode path" + path);
        } else {
            System.out.println(path + "not exists");
        }
    }

    public static void getChildrenNode(String path, ZooKeeper zk)
            throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren(path, true);
        for (String subPath : children) {
            System.out.println("getChildrenNode:" + path + "----" + subPath);
        }
    }

}
