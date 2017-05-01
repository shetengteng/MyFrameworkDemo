package com.stt.webConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.stt.log.aspect.MyLogAspect;

@Configuration
@ComponentScan(basePackages = { "com.stt.log" })
// 启用AspectJ自动代理
@EnableAspectJAutoProxy
public class MyLogConfig {

	@Bean
	public MyLogAspect myLogAspect() {
		return new MyLogAspect();
	}

}
