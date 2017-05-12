package com.stt.curator.demo02_watcher;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Test01_NodeCacheListener {

    public static void main(String[] args) {

        String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 5000;

        String path = "/curator/nodeCacheListener";

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

            // 建立一个cache缓存，第三个是数据是否压缩
            final NodeCache nodeCache = new NodeCache(cf, path, false);
            // 初始化建立
            boolean buildInitial = true;
            nodeCache.start(buildInitial);
            // 进行监听操作,可以指定线程池
            ExecutorService pool = Executors.newCachedThreadPool();
            // 监听节点的变化
            NodeCacheListener listener = new NodeCacheListener() {
                // 触发事件为创建节点和更新节点，在删除节点的时候并不触发此操作
                @Override
                public void nodeChanged() throws Exception {
                    System.out.println("路径为："
                            + nodeCache.getCurrentData().getPath());
                    System.out.println("数据为："
                            + new String(nodeCache.getCurrentData().getData()));
                    System.out.println("状态为："
                            + nodeCache.getCurrentData().getStat());
                }
            };
            nodeCache.getListenable().addListener(listener, pool);

            // 创建节点
            Thread.sleep(1000);
            cf.create().forPath(path);

            // 修改节点
            Thread.sleep(1000);
            cf.setData().forPath(path, "setData".getBytes());

            // 删除节点
            Thread.sleep(1000);
            // cf.delete().forPath(path);

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            cf.close();
        }
    }
}
