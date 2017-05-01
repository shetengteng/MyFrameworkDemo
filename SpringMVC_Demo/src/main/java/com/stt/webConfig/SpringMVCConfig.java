package com.stt.webConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
// proxyTargetClass=true 使用CGLB代理模式
@EnableAspectJAutoProxy
// 等价于在springmvc.xml中配置了 <mvc:annotation-driven>
@EnableWebMvc
// 配置扫描的包，扫描controller层中的controller注解的类，不使用默认的过滤器
@ComponentScan(basePackages = { "com.stt.controller" }, useDefaultFilters = false, includeFilters = @Filter(Controller.class))
public class SpringMVCConfig extends WebMvcConfigurerAdapter {

	// 配置静态资源处理
	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		System.out
				.println("SpringMVCConfig----configureDefaultServletHandling");
		// 要求DispatcherServlet将对静态资源的请求转发到Servlet容器中的默认的Servlet上
		// 不使用DispatcherServlet本身来进行处理
		configurer.enable();
	}

	// 配置一个JSP视图解析器
	// 添加前后缀
	@Bean
	public ViewResolver viewResolver() {
		System.out.println("SpringMVCConfig----viewResolver");
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/jsp/");
		resolver.setSuffix(".jsp");
		// 将视图解析为jstlView
		resolver.setViewClass(JstlView.class);
		resolver.setExposeContextBeansAsAttributes(true);
		return resolver;
	}

}
