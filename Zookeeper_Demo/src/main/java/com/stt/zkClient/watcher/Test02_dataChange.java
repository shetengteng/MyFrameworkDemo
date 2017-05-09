package com.stt.zkClient.watcher;

import org.I0Itec.zkclient.DataUpdater;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkMarshallingError;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.CreateMode;

public class Test02_dataChange {

    public static void main(String[] args) throws InterruptedException {

        String connectAddr = "172.28.14.69:2181,172.28.14.69:2182,172.28.14.69:2183";
        int sessionTimeout = 5000;
        int connectTimeout = 2000;
        ZkConnection connection = new ZkConnection(connectAddr, sessionTimeout);
        ZkClient zkClient = new ZkClient(connection, connectTimeout);

        String path = "/zkClientTest";
        // 查看数据变化
        // 注意，这里的变化只有注册的path路径下的数据发生了变化才会被监听到
        // 而path下的子路径发生了数据变化不会被监听到
        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            @Override
            public void handleDataChange(String dataPath, Object data)
                    throws Exception {
                System.out.println("节点：" + dataPath + " 数据发生变化 数值：" + data);
            }

            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("节点：" + dataPath + " 被删除");
            }
        });

        // 自定义解析器
        zkClient.setZkSerializer(new ZkSerializer() {

            @Override
            public byte[] serialize(Object data) throws ZkMarshallingError {
                return data.toString().getBytes();
            }

            @Override
            public Object deserialize(byte[] bytes) throws ZkMarshallingError {
                return new String(bytes);
            }

        });

        // 创建节点
        zkClient.create(path, "test", CreateMode.PERSISTENT);
        Thread.sleep(1000);

        // 修改已经序列化的数据，先从path中获取，然后再修改
        // 特殊的更新操作
        zkClient.updateDataSerialized(path, new DataUpdater<Object>() {
            @Override
            public Object update(Object currentData) {
                return currentData.toString() + "_stt";
            }
        });

        // 创建级联节点
        zkClient.createPersistent(path + "/test", true);
        zkClient.writeData(path + "/test", "new");
        Thread.sleep(1000);

        zkClient.writeData(path, "new");

        Thread.sleep(1000);

        // 级联删除
        // zkClient.deleteRecursive(path);
        // 注意：这里需要添加睡眠，否则，删除的监听操作会在主线程结束而没有触发到
        Thread.sleep(10000);
    }
}
