package com.stt.ch02_loadBalancing;

import java.util.List;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;

public class Consumer02 {

    public Consumer02() {
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

            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    public static void main(String[] args) {
        Consumer02 c2 = new Consumer02();
        System.out.println("c2 start...");
    }

}
