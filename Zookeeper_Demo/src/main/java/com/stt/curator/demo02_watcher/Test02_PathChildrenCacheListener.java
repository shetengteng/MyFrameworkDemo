package com.stt.curator.demo02_watcher;

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

public class Test02_PathChildrenCacheListener {

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

            // 建立一个cache缓存，第三个是节点的数据是否存储
            final PathChildrenCache nodeCache = new PathChildrenCache(cf, path,
                    true);
            // 在初始化之后进行缓存的监听
            nodeCache.start(StartMode.POST_INITIALIZED_EVENT);

            // 进行监听操作,可以指定线程池
            ExecutorService pool = Executors.newCachedThreadPool();

            PathChildrenCacheListener listener = new PathChildrenCacheListener() {
                // 监听子节点的新建，删除，修改
                @Override
                public void childEvent(CuratorFramework client,
                        PathChildrenCacheEvent event) throws Exception {
                    System.out.println("----start----");
                    System.out.println("触发的类型：" + event.getType());
                    System.out.println("触发的路径：" + event.getData().getPath());
                    System.out.println("触发类型的值："
                            + new String(event.getData().getData()));
                    switch (event.getType()) {
                    case INITIALIZED:
                        break;
                    case CHILD_ADDED:
                        break;
                    case CHILD_REMOVED:
                        break;
                    case CHILD_UPDATED:
                        break;
                    case CONNECTION_LOST:
                        break;
                    case CONNECTION_RECONNECTED:
                        break;
                    case CONNECTION_SUSPENDED:
                        break;
                    default:
                        break;
                    }
                    System.out.println("----end----");
                    System.out.println();
                }
            };

            nodeCache.getListenable().addListener(listener, pool);

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

            // 递归删除
            Thread.sleep(1000);
            cf.delete().deletingChildrenIfNeeded().forPath(path);

            Thread.sleep(10000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            cf.close();
        }
    }
}
