package com.stt.ch02_loadBalancing;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

// 测试负载均衡的特性，一个producer 2个consumer（在同一个group中）
// 天然的实现了负载均衡

public class Producer {

    public static void main(String[] args) throws MQClientException,
            InterruptedException {

        String group_name = "message_producer";
        DefaultMQProducer producer = new DefaultMQProducer(group_name);
        String namesrvAddr = "192.168.0.121:9876;192.168.0.0.122:9876";
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();

        // 发送消息
        for (int i = 0; i < 100; i++) {
            try {
                Message msg = new Message("model_topic", "tag01",
                        ("message info:" + i).getBytes());
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);

            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
    }
}
