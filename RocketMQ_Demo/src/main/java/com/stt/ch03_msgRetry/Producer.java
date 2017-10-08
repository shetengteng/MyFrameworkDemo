package com.stt.ch03_msgRetry;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

//producer发送一个消息
//当consumer01 收到消息后，睡眠60s，consumer02开始接受消息，此时consumer01关闭，consumer02收到该消息
// mq判断该消息第一次发送失败，rocketmq消息重发机制

public class Producer {

    public static void main(String[] args) throws MQClientException,
            InterruptedException {

        String group_name = "message_producer";
        DefaultMQProducer producer = new DefaultMQProducer(group_name);
        String namesrvAddr = "192.168.0.121:9876;192.168.0.0.122:9876";
        producer.setNamesrvAddr(namesrvAddr);
        producer.start();

        // 发送消息
        for (int i = 0; i < 1; i++) {
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
