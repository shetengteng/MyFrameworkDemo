package com.stt.curator.demo03_cluster;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class CuratorWatcher {

    private String connectAddr = "192.168.0.119:2181,192.168.0.119:2182,192.168.0.119:2183";
    private int sessionTimeout = 5000;
    private int connectTimeout = 5000;
    private String path = "/curator/systemConf";
    // 进行监听操作,可以指定线程池
    private ExecutorService pool = Executors.newCachedThreadPool();

    private PathChildrenCacheListener listener = new PathChildrenCacheListener() {
        // 监听子节点的新建，删除，修改
        @Override
        public void childEvent(CuratorFramework client,
                PathChildrenCacheEvent event) throws Exception {
            System.out.println("----start----");
            System.out.println("触发的类型：" + event.getType());
            switch (event.getType()) {
            case CHILD_ADDED:
                System.out.println("路径：" + event.getData().getPath());
                System.out.println("节点值："
                        + new String(event.getData().getData()));
                break;
            case CHILD_REMOVED:
                System.out.println("路径：" + event.getData().getPath());
                System.out.println("节点值："
                        + new String(event.getData().getData()));
                break;
            case CHILD_UPDATED:
                System.out.println("路径：" + event.getData().getPath());
                System.out.println("节点值："
                        + new String(event.getData().getData()));
                break;
            default:
                break;
            }
            System.out.println("---end---");
        }
    };

    public CuratorWatcher() {
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
            // 如果节点不存在，需要创建
            if (cf.checkExists().forPath(path) == null) {
                cf.create().withMode(CreateMode.PERSISTENT)
                        .forPath(path, "init".getBytes());
            }
            // 建立一个cache缓存，第三个是节点的数据是否存储
            PathChildrenCache nodeCache = new PathChildrenCache(cf, path, true);
            // 在初始化之后进行缓存的监听
            nodeCache.start(StartMode.POST_INITIALIZED_EVENT);
            nodeCache.getListenable().addListener(listener, pool);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 在生产中，需要一直开启，监听变化，不需要关闭链接
            // cf.close();
        }
    }
}
