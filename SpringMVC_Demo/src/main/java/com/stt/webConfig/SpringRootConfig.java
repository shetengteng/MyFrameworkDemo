package com.stt.webConfig;

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
public class SpringRootConfig {

}
