package com.stt.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stt.ActiveMQ_Spring.entity.Mail;
import com.stt.ActiveMQ_Spring.producer.service.MailProducerService;

@ContextConfiguration(locations = { "classpath:spring-context.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class TestProducer {

	@Autowired
	private MailProducerService mailProducerService;

	@Test
	public void sendMail() {
		try {
			Mail mail = new Mail();
			mail.setContent("Hello World!");
			mail.setSubject("mail test");
			mail.setTo("382143256@qq.com");
			mailProducerService.sendMail(mail);
			System.out.println("发送结束...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
