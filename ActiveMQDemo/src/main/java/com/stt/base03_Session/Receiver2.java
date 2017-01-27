package com.stt.base03_Session;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver2 {
	public static void main(String[] args) {
		try {
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("stt", "stt", "tcp://localhost:61616");
			Connection conn = connectionFactory.createConnection();
			conn.start();
			Session session = conn.createSession(false, Session.CLIENT_ACKNOWLEDGE);
			Destination destination = session.createQueue("helloWorldQueue");
			MessageConsumer msgConsumer = session.createConsumer(destination);
			for (;;) {
				Message msg = msgConsumer.receive();
				if (msg == null)
					break;
				// 手动签收消息，另启动一个线程，通知MQ服务确认签收
				msg.acknowledge();
				System.out.println("consumer received:" + ((TextMessage) msg).getText());
			}
			if (conn != null) {
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
