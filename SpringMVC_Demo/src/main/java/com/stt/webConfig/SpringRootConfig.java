package com.stt.webConfig;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;

@Configuration
// 定义扫描的包。以及要排除的注解类
@ComponentScan(basePackages = { "com.stt.service" }, excludeFilters = {
		@Filter(Controller.class), @Filter(Configuration.class) })
// @Import(value = { ConcurrentMapCacheConfig.class })
@Import(value = {
// 导入的参数
		MyCacheConfig.class, // 导入自定义缓存
		MyLogConfig.class, // 导入自定义日志
		RedisConfig.class // 导入redis配置
})
public class SpringRootConfig implements ApplicationContextAware {

	// 配置PropertySource扫描,没有也可以扫描得到，可能是默认配置的问题
	// @Bean
	// public static PropertySourcesPlaceholderConfigurer
	// propertySourcesPlaceholderConfigurer() {
	// return new PropertySourcesPlaceholderConfigurer();
	// }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// 通过实现 ApplicationContextAware 接口，可以获取ioc容器，侵入式注入

	}

}
