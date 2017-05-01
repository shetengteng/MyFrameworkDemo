package com.stt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.stt.log.annotation.MyLog;
import com.stt.service.AccountService;

@Controller
public class HomeController {

	@Autowired
	private AccountService accountService;

	// 返回主页
	// requestMapping中可以配置多个映射路径
	@RequestMapping(value = { "/index" }, method = RequestMethod.GET)
	@MyLog(description = "HomeController 日志")
	public String home() {
		System.out.println("HomeController----home");
		accountService.getAccount(1);
		return "index";
	}

}
