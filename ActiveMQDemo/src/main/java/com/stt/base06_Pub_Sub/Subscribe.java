package com.stt.base06_Pub_Sub;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Subscribe {

	private ConnectionFactory connFactory;
	private Connection conn;
	private Session session;
	private MessageConsumer consumer;

	public Subscribe() {
		try {
			connFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
					ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://localhost:61616");
			conn = connFactory.createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receivedMessage() throws JMSException {
		Destination topic = session.createTopic("topic01");
		consumer = session.createConsumer(topic);
		consumer.setMessageListener(new MyListener());
	}

	// 添加监听消息的线程
	private class MyListener implements MessageListener {
		@Override
		public void onMessage(Message msg) {
			try {
				if (msg instanceof TextMessage) {
					TextMessage ret = (TextMessage) msg;
					System.out.println("received:" + ret.getText());
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			Subscribe subscribe = new Subscribe();
			subscribe.receivedMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
