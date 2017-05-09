package com.stt.zookeeper.demo04_acl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

public class Test02_test_digest {

    // 用于建立连接，异步变同步使用
    private static final CountDownLatch countDownLatch = new CountDownLatch(1);
    // 定义原子变量，用于记录次数
    private static AtomicInteger seq = new AtomicInteger();
    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
    private static String auth = "admin:admin";// 组织形式任意，是一个字符串即可

    private static Watcher watcher = new Watcher() {
        @Override
        public void process(WatchedEvent event) {
            System.out.println("--进入了 process 的线程--" + seq.incrementAndGet());
            System.out.println("连接的状态：" + event.getState());
            System.out.println("事件的类型：" + event.getType());
            System.out.println("事件触发的路径：" + event.getPath());

            if (KeeperState.SyncConnected == event.getState()) {
                if (EventType.None.equals(event.getType())) {
                    countDownLatch.countDown();
                    System.out.println("---建立连接---");
                }
            } else if (KeeperState.AuthFailed == event.getState()) {
                System.out.println("认证失败");
            } else if (KeeperState.Expired == event.getState()) {
                System.out.println("连接超时");
            } else if (KeeperState.Disconnected == event.getState()) {
                System.out.println("断开链接");
            }
        }
    };

    public static void main(String[] args) {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(connectStr, 2000, watcher);
            // 添加权限
            countDownLatch.await();

            // 在没有认证的情况下创建子节点
            try {
                zk.create("/testACL/test01", "testACL".getBytes(),
                        Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (Exception e) {
                System.out.println("ERROR01:" + e.getMessage());
            }
            // 结果：ERROR01:KeeperErrorCode = NoAuth for /testACL/test01

            // 在没有权限的情况下读取节点的信息
            try {
                byte[] data = zk.getData("/testACL", false, null);
                System.out.println("RESULT02:" + new String(data));
            } catch (KeeperException e) {
                System.out.println("ERROR02:" + e.getMessage());
            }
            // 结果：RESULT02:testACL

            // 在正常获取权限的情况下创建子节点
            try {
                zk.addAuthInfo("digest", auth.getBytes());
                zk.create("/testACL/test01", "testACL".getBytes(), getAcls(),
                        CreateMode.PERSISTENT);
            } catch (Exception e) {
                System.out.println("ERROR03:" + e.getMessage());
            }
            // 结果：成功

            // 更新数据
            try {
                zk.addAuthInfo("digest", auth.getBytes());
                if (zk.exists("/testACL", false) != null) {
                    zk.setData("/testACL", "newValue".getBytes(), -1);
                }
            } catch (KeeperException e) {
                System.out.println("ERROR04:" + e.getMessage());
            }
            // 结果：成功

            // 在没有权限的情况下删除
            try {
                zk.addAuthInfo("digest", null);
                zk.delete("/testACL/test01", -1);
            } catch (Exception e) {
                System.out.println("ERROR05:" + e.getMessage());
            }
            // 结果：ERROR05:KeeperErrorCode = AuthFailed for /testACL/test01

        } catch (IOException | InterruptedException e) {
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

    // 获取ACL认证信息
    // 可以从数据库中获取对应的用户名以及密码和对应的权限，在此处赋值
    private static List<ACL> getAcls() throws NoSuchAlgorithmException {
        List<ACL> acls = new ArrayList<>();
        // 添加采用用户名密码的方式
        Id id01 = new Id();
        id01.setScheme("digest");
        id01.setId(DigestAuthenticationProvider.generateDigest(auth));
        ACL acl01 = new ACL();
        // 设置权限,ALL
        acl01.setPerms(ZooDefs.Perms.ALL);
        acl01.setId(id01);
        acls.add(acl01);

        // 设置开放权限模式为只读
        Id id02 = new Id("world", "anyone");
        ACL acl02 = new ACL(ZooDefs.Perms.READ, id02);
        acls.add(acl02);

        return acls;
    }
}
