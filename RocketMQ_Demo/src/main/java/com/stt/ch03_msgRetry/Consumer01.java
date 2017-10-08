package com.stt.ch03_msgRetry;

import java.util.List;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;

public class Consumer01 {

    public Consumer01() {
        try {
            String groupName = "message_consumer";
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(
                    groupName);
            String namesrvAddr = "192.168.0.121:9876;192.168.0.0.122:9876";
            consumer.setNamesrvAddr(namesrvAddr);
            // 订阅主题，并且可以通过标签进行筛选
            consumer.subscribe("model_topic", "tag01 || tag02 || tag03");

            consumer.registerMessageListener(new MsgListener());
            consumer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 定义监听器
    class MsgListener implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                ConsumeConcurrentlyContext context) {
            try {
                MessageExt msg = msgs.get(0);
                String topic = msg.getTopic();
                String msgBody = new String(msg.getBody(), "utf-8");
                String tags = msg.getTags();
                System.out.println("topic:" + topic + " tags:" + tags + " msg:"
                        + msgBody);

                Thread.sleep(60000);

            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public static void main(String[] args) {
        Consumer01 c1 = new Consumer01();
        System.out.println("c1 start...");
        // 不能同时开启2个 订阅 同一个进程只能开启一个group的consumer
        /**
         * com.alibaba.rocketmq.client.exception.MQClientException: The consumer
         * group[message_consumer] has been created before, specify another name
         * please. See https://github.com/alibaba/RocketMQ/issues/40 for further
         * details.c2 start...
         * 
         * at
         * com.alibaba.rocketmq.client.impl.consumer.DefaultMQPushConsumerImpl
         * .start(DefaultMQPushConsumerImpl.java:714) at
         * com.alibaba.rocketmq.client
         * .consumer.DefaultMQPushConsumer.start(DefaultMQPushConsumer.java:365)
         * at com.stt.ch02_model.Consumer01.<init>(Consumer01.java:24) at
         * com.stt.ch02_model.Consumer01.main(Consumer01.java:57)
         */
        // Consumer01 c2 = new Consumer01();
        // System.out.println("c2 start...");
    }

}
