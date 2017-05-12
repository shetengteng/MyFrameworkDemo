package com.stt.curator.demo03_cluster;

public class CuratorClient02 {

    public static void main(String[] args) {

        CuratorWatcher watcher = new CuratorWatcher();
        for (;;) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
