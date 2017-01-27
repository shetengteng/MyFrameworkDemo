package com.stt.base04_MsgProducer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	public static void main(String[] args) {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("stt", "stt", "tcp://localhost:61616");
			Connection conn = connectionFactory.createConnection();
			conn.start();
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 创建生产者可以不指定目的地，在发送的时刻可以设定
			MessageProducer msgProducer = session.createProducer(null);
			for (int i = 0; i < 5; i++) {
				TextMessage msg = session.createTextMessage();
				msg.setText("helloWorld-P2P id:" + i);
				// 发送消息时可以指定发送的目的地，是否持久化，权重，以及超时时间
				msgProducer.send(session.createQueue("helloWorldQueue"), msg, DeliveryMode.NON_PERSISTENT, i,
						1000 * 60 * 2);
				System.out.println("sender send msg -->" + msg.getText());
			}
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
