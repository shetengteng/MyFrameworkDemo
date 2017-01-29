package com.stt.base03_Session;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
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
			// 3.通过connection对象创建session会话，以后的操作都在session上进行处理
			// 接收和发送消息，参数配置1.表示是否启用事务，参数配置2，为签收模式，一般设置为自动签收
			Session session = conn.createSession(true, Session.AUTO_ACKNOWLEDGE);

			Destination destination = session.createQueue("helloWorldQueue");
			MessageProducer msgProducer = session.createProducer(destination);
			msgProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			for (int i = 0; i < 5; i++) {
				TextMessage msg = session.createTextMessage();
				msg.setText("helloWorld-P2P id:" + i);
				msgProducer.send(msg);
				System.out.println("sender send msg -->" + msg.getText());
			}

			// 注意：使用了事务的方式，这里的session需要提交,否则消息不会被发送
			session.commit();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
