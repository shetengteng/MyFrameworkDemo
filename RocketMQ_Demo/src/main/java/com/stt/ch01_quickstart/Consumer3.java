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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stt.ch01_quickstart;

import java.util.List;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;

/**
 * Consumer，订阅消息
 */
public class Consumer3 {

    public static void main(String[] args) throws InterruptedException,
            MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(
                "quickstart_consumer");
        consumer.setNamesrvAddr("192.168.0.121:9876;192.168.0.122:9876");
        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果非第一次启动， 那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 订阅相同的主题，和tag进行过滤操作
        consumer.subscribe("quickstart_topic", "*");
        // 批量处理10条数据，指的是producer先发送批量数据，consumer再启动的时候进行获取数据，
        // 如果consumer消费的速度和发送的速度一致，那么接收到的数据就是一条一条的
        // 该值默认值是1
        // consumer.setConsumeMessageBatchMaxSize(10);
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                try {
                    // System.out.println(Thread.currentThread().getName()
                    // + " Receive New Messages: " + msgs);
                    // 注意：这里msgs的大小是1，消息是一个一个的接收的
                    String topic = msg.getTopic();
                    String msgBody = new String(msg.getBody(), "utf-8");
                    String tags = msg.getTags();
                    System.out.println("收到的消息-topic:" + topic + ",tags:" + tags
                            + ",msg:" + msgBody);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (msg.getReconsumeTimes() == 2) {
                        System.out.println("重试2次依然失败");
                        // 记录日志到数据库，然后由数据库的存储过程进行消息的处理
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    // 失败的情况下，稍后再进行发送，发送该返回码之后重试
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        System.out.println("Consumer Started.");
    }
}
