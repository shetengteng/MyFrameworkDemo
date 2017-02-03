package com.stt.ActiveMQ_Spring.consumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.stt.ActiveMQ_Spring.entity.Mail;

@Service("mailConsumerService")
public class MailConsumerService {

	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private SimpleMailMessage simpleMailMessage;
	@Autowired
	private ThreadPoolTaskExecutor threadPool;

	public void sendMail(final Mail mail) {
		// 使用线程池，处理发送邮件的任务
		threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					simpleMailMessage.setTo(mail.getTo());
					simpleMailMessage.setSubject(mail.getSubject());
					simpleMailMessage.setText(mail.getContent());
					// mailSender.send(simpleMailMessage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
