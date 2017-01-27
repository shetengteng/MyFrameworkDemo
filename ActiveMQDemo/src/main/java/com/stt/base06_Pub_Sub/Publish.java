package com.stt.base06_Pub_Sub;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Publish {

	private ConnectionFactory connFactory;
	private Connection conn;
	private Session session;
	private MessageProducer producer;

	public Publish() {
		try {
			connFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
					ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://localhost:61616");
			conn = connFactory.createConnection();
			conn.start();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			producer = session.createProducer(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String msgStr) throws JMSException {
		Destination topic = session.createTopic("topic01");
		TextMessage msg = session.createTextMessage();
		msg.setText(msgStr);
		producer.send(topic, msg);
	}

	public void release() throws JMSException {
		if (conn != null)
			conn.close();
	}

	public static void main(String[] args) {
		try {
			Publish publish = new Publish();
			publish.sendMessage("hello PUB_SUB");
			publish.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
