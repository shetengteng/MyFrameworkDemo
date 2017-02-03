package com.stt.ActiveMQ_Spring.consumer.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stt.ActiveMQ_Spring.consumer.service.MailConsumerService;
import com.stt.ActiveMQ_Spring.entity.Mail;

@Component
public class MailQueueMessageListener implements SessionAwareMessageListener<Message> {

	@Autowired
	private JmsTemplate jmsTemplate;
	// @Autowired
	// private Destination mailQueue;
	@Autowired
	private MailConsumerService mailConsumerService;

	@Override
	public void onMessage(Message msg, Session session) throws JMSException {
		try {
			TextMessage textMsg = (TextMessage) msg;
			String msgStr = textMsg.getText();
			System.out.println("接受的到的消息：" + msgStr);
			// 转换成相应的对象
			ObjectMapper mapper = new ObjectMapper();
			Mail mail = mapper.readValue(msgStr, Mail.class);
			if (mail == null)
				throw new RuntimeException("mail is null");
			try {
				mailConsumerService.sendMail(mail);
			} catch (Exception e) {
				// 如果发生异常，可以重新放回队列中
				e.printStackTrace();
				// jmsTemplate.send(mailQueue, new MessageCreator() {
				// @Override
				// public Message createMessage(Session session) throws
				// JMSException {
				// return session.createTextMessage(ms);
				// }
				// });
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
