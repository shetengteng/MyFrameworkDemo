package com.stt.service;

import com.stt.pojo.Account;

public interface AccountService {

	public Account getAccount(int id);

	public Account getAccount(Account queryModel);

}
