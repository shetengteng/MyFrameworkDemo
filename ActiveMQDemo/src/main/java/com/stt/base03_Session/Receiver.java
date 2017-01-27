package com.stt.base03_Session;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {
	public static void main(String[] args) {
		try {
			// 1. 建立ConnectionFactory工厂对象，输入用户名，密码，连接的地址
			// 使用默认的端口和地址是tcp://localhost:61616
			// 注意：示例中的ActiveMQConnectionFactory不是activemq.spring下的
			// 这里使用认证的用户名和密码，以及访问的连接
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("stt", "stt", "tcp://localhost:61616");
			// 2.通过ConnectionFactory工厂对象，创建一个connection连接，调用连接的start方法开启连接
			// 连接默认是关闭的，创建连接可以使用输入用户名和密码的方式，这里创建工厂时已经提供了，就使用另一个方式创建连接
			Connection conn = connectionFactory.createConnection();
			conn.start();
			// 3.通过connection对象创建session会话，以后的操作都在session上进行处理
			// 接收和发送消息，参数配置1.表示是否启用事务，参数配置2，为签收模式，一般设置为自动签收
			// 这里示例不启用事务，如果启用了事务，那么消费者没有消费，则消息会撤销，在P2P中，等于是同步的消息模式
			Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 4.通过Session创建Destination对象，是一个客户端用来指定生产消息目标和消费来源的对象
			// 在P2P模式中，Destination为Queue，在Pub/Sub模式中，Destination被称为Topic
			// 在程序中可以有多个Queue和Topic
			Destination destination = session.createQueue("helloWorldQueue");
			// 5.通过session对象创建消息的发送和接收对象MessageProducer/MessageConsumer
			// 将Destination作为入参放初始化消费者对象
			MessageConsumer msgConsumer = session.createConsumer(destination);
			// 6.接收消息，读取所有的消息
			for (;;) {
				Message msg = msgConsumer.receive();
				if (msg == null)
					break;
				System.out.println("consumer received:" + ((TextMessage) msg).getText());
			}
			// 最后需要释放连接
			if (conn != null) {
				// 关闭connection时，会递归的关闭session等
				conn.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
