package com.stt.base05_MsgConsumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 设计消费者，在初始化的时候确定队列或者主题，然后进行监听 ，如果需要重新制定主题，需要将原先的释放，然后重新建立连接和监听
 * 
 * @author Administrator
 *
 */
public class Consumer {

	// 1.连接工厂
	private ConnectionFactory connectionFactory;
	// 2.连接对象
	private Connection connection;
	// 3.会话对象
	private Session session;
	// 4.消费者
	private MessageConsumer msgConsumer;
	// 5.目的地址
	private Destination destination;

	public Consumer(String queueName, String selector) {
		try {
			this.connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnectionFactory.DEFAULT_USER,
					ActiveMQConnectionFactory.DEFAULT_PASSWORD, "tcp://localhost:61616");
			this.connection = connectionFactory.createConnection();
			this.connection.start();
			// 自动提交，自动签收
			this.session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
			this.destination = session.createQueue(queueName);
			this.msgConsumer = this.session.createConsumer(this.destination, selector);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void receivedMessage() throws JMSException {
		msgConsumer.setMessageListener(new MyListener());
	}

	// 添加监听消息的线程
	private class MyListener implements MessageListener {
		@Override
		public void onMessage(Message msg) {
			try {
				if (msg instanceof TextMessage) {
					TextMessage ret = (TextMessage) msg;
					System.out.println("received:" + ret.getText());
				} else if (msg instanceof MapMessage) {
					MapMessage ret = (MapMessage) msg;
					System.out.println(ret.toString());
					System.out.println(
							ret.getString("name") + ":" + ret.getString("age") + ":" + ret.getStringProperty("color"));
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		try {
			// 注意：只对含有Property的属性参数有效，例如age就没有效果
			String queueName = "queue01";
			String selector01 = "color = 'blue' AND sal > 2000";
			Consumer consumer = new Consumer(queueName, selector01);
			consumer.receivedMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
