package com.stt.zookeeper.demo05_cluster;

public class Client02 {

    private static String connectStr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
    private static String configPath = "/configRoot";

    public static void main(String[] args) throws InterruptedException {
        ZKConfigWatcher watcher = new ZKConfigWatcher(connectStr, configPath);
        for (;;) {
            Thread.sleep(10000);
        }
    }

}
