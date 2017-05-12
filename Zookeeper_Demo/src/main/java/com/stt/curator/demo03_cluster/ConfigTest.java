package com.stt.curator.demo03_cluster;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ConfigTest {

    public static void main(String[] args) {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        String path = "/curator/systemConf";

        // 设置重连策略,初始时间为1s,重试10次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);

        // 使用静态工厂类方法创建，可以使用链式编程风格，注意在最后使用build方法生成
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(connectAddr).connectionTimeoutMs(connectTimeout)
                .sessionTimeoutMs(sessionTimeout).retryPolicy(retryPolicy)
                .build();

        try {
            // 开启连接
            cf.start();

            // 创建节点,需要判断是否已经存在，如果存在则会抛出异常
            Thread.sleep(1000);
            if (null == cf.checkExists().forPath(path + "/s1")) {
                cf.create().forPath(path + "/s1", "inital".getBytes());
            }
            Thread.sleep(1000);
            if (null == cf.checkExists().forPath(path + "/s2")) {
                cf.create().forPath(path + "/s2", "inital".getBytes());
            }

            // 修改节点
            Thread.sleep(1000);
            cf.setData().forPath(path + "/s1", "newValue".getBytes());

            // 删除节点
            Thread.sleep(1000);
            cf.delete().forPath(path + "/s2");

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
