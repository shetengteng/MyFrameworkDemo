/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is istributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stt.quickstart;

import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;

/**
 * Producer，发送消息
 * 
 */
public class Producer {
    public static void main(String[] args) throws MQClientException,
            InterruptedException {
        // 全局唯一的groupName
        DefaultMQProducer producer = new DefaultMQProducer(
                "quickstart_producer");
        // 设置nameserver的地址
        producer.setNamesrvAddr("192.168.0.121:9876;192.168.0.122:9876");
        producer.start();

        // 发送消息
        for (int i = 0; i < 100; i++) {
            try {
                Message msg = new Message("quickstart_topic",// topic 主题
                        "TagA",// tag 标签
                        ("Hello RocketMQ " + i).getBytes()// body 消息的内容
                );
                SendResult sendResult = producer.send(msg);
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        // 释放资源
        producer.shutdown();
    }
}
