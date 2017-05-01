package com.stt.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.stt.log.annotation.MyLog;
import com.stt.pojo.Account;

@Service
public class AccountServiceImpl implements AccountService {

	@Override
	@Cacheable(value = "myCache", key = "#root.methodName+'_'+#id")
	@MyLog(description = "日志测试")
	// @Cacheable(value = "concurrentCache", key = "#id")
	public Account getAccount(int id) {
		// 模拟从数据库中获取
		Account account = new Account("stt");
		account.setId(id);
		System.out.println("----getAccount----");
		return account;
	}

	@Override
	// 自定义key值，如果传入的是查询对象，则可以使用该对象的hash值
	@Cacheable(value = "myCache", key = "#root.methodName+'_'+#queryModel.hashCode()")
	public Account getAccount(Account queryModel) {
		// 模拟从数据库中获取
		Account account = new Account("stt2");
		account.setId(queryModel.getId());
		System.out.println("----getAccount2----");
		return account;
	}

}
