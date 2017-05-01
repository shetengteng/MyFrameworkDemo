package com.stt.webConfig;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * 定义自己的web容器初始化，替代web.xml配置
 * @author Administrator
 *
 */
public class MyWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected String[] getServletMappings() {
		// 将SpringMVC的DispatcherServlet 映射到 / 的路径
		// 可以配置多个映射路径
		return new String[] { "/" };
	}

	// 配置Spring 容器的Config类，等价于ContextLoaderListener中初始化的ioc容器
	@Override
	protected Class<?>[] getRootConfigClasses() {
		System.out.println("MyWebInitializer-----getRootConfigClasses");
		return new Class<?>[] { SpringRootConfig.class };
	}

	// 配置SpringMVC容器的Config类
	@Override
	protected Class<?>[] getServletConfigClasses() {
		System.out.println("MyWebInitializer-----getServletConfigClasses");
		return new Class<?>[] { SpringMVCConfig.class };
	}

}
