package com.stt.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.stt.webConfig.SpringRootConfig;

//SpringJUnit4ClassRunner 该类在测试开始时，自动创建Spring的应用上下文
@RunWith(SpringJUnit4ClassRunner.class)
// 上下文中需要加载的配置信息
@ContextConfiguration(classes = SpringRootConfig.class)
public class BaseTest {

	@Test
	public void base() {

	}

}
