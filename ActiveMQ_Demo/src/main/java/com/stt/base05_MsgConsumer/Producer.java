package com.stt.base05_MsgConsumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Producer {

	// 1.连接工厂
	private ConnectionFactory connectionFactory;
	// 2.连接对象
	private Connection connection;
	// 3.会话对象
	private Session session;
	// 4.生产者
	private MessageProducer msgProducer;

	public Producer() {
		try {
			this.connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
					ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://localhost:61616");
			this.connection = connectionFactory.createConnection();
			this.connection.start();
			// 自动提价，自动签收
			this.session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
			this.msgProducer = this.session.createProducer(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Session getSession() {
		return this.session;
	}

	public void sendToQueue(String queueName, Message message) {
		try {
			Destination queue = this.session.createQueue(queueName);
			// 设置是否持久化，权重（0-9），超时时间（设置为0时，表示永不过期）
			// this.msgProducer.send(queue, message,
			// DeliveryMode.NON_PERSISTENT, 4, 1000 * 60 * 2);
			// 用于测试连接mysql的
			this.msgProducer.send(queue, message, DeliveryMode.PERSISTENT, 4, 1000 * 60 * 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			Producer producer = new Producer();
			// 使用带有Property后缀的属性，用于消息的过滤的选择
			// 而没有Property的属性，则是消息的包含的信息
			MapMessage msg1 = producer.getSession().createMapMessage();
			msg1.setString("name", "张三");
			msg1.setString("age", "23");
			msg1.setStringProperty("color", "blue");
			msg1.setIntProperty("sal", 12000);

			MapMessage msg2 = producer.getSession().createMapMessage();
			msg2.setString("name", "李四");
			msg2.setString("age", "24");
			msg2.setStringProperty("color", "red");
			msg2.setIntProperty("sal", 1000);

			MapMessage msg3 = producer.getSession().createMapMessage();
			msg3.setString("name", "王五");
			msg3.setString("age", "25");
			msg3.setStringProperty("color", "yellow");
			msg3.setIntProperty("sal", 12000);

			producer.sendToQueue("queue01", msg1);
			producer.sendToQueue("queue01", msg2);
			producer.sendToQueue("queue01", msg3);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
