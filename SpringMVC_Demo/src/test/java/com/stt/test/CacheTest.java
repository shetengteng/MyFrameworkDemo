package com.stt.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stt.pojo.Account;
import com.stt.service.AccountService;
import com.stt.webConfig.SpringRootConfig;

//SpringJUnit4ClassRunner 该类在测试开始时，自动创建Spring的应用上下文
@RunWith(SpringJUnit4ClassRunner.class)
// 上下文中需要加载的配置信息
@ContextConfiguration(classes = SpringRootConfig.class)
public class CacheTest {

	@Autowired
	private AccountService accountService;

	@Test
	public void cacheAbleTest() {
		Account account = accountService.getAccount(1);
		System.out.println("-----" + account);
		accountService.getAccount(1);
		System.out.println("-----");
		accountService.getAccount(1);
		System.out.println("-----");
		accountService.getAccount(1);
		System.out.println("-----");
		accountService.getAccount(2);

	}

	@Test
	public void cacheAbleTest2() {
		Account query = new Account("stt2");
		query.hashCode();
		query.setId(1);
		Account account = accountService.getAccount(query);
		System.out.println("-----" + account);
		accountService.getAccount(query);
		System.out.println("-----");
		accountService.getAccount(query);
		System.out.println("-----");
		accountService.getAccount(query);
		System.out.println("-----");
		accountService.getAccount(query);

	}
}
