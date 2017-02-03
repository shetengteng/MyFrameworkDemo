package com.stt.ActiveMQ_Spring.producer.service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stt.ActiveMQ_Spring.entity.Mail;

@Service("mailProducerService")
public class MailProducerService {

	@Autowired
	private JmsTemplate jmsTemplate;

	public void sendMail(final Mail mail) {
		jmsTemplate.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMapper mapper = new ObjectMapper();
				String jsonStr;
				try {
					jsonStr = mapper.writeValueAsString(mail);
					return session.createTextMessage(jsonStr);
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

}
