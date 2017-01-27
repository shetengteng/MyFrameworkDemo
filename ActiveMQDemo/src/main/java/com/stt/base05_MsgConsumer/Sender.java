package com.stt.base05_MsgConsumer;

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
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
					ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://localhost:61616");
			Connection conn = connectionFactory.createConnection();
			conn.start();
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer msgProducer = session.createProducer(null);
			for (int i = 0; i < 5; i++) {
				TextMessage msg = session.createTextMessage();
				msg.setText("helloWorld-P2P id:" + i);
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
