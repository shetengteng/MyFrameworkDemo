package com.stt.bak;

import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import com.stt.webConfig.SpringMVCConfig;
import com.stt.webConfig.SpringRootConfig;

/**
 * Web应用初始化，自定义初始化操作，含有添加过滤器等
 * 
 */
public class WebInitializer implements WebApplicationInitializer {
	private static final Logger logger = LoggerFactory
			.getLogger(WebInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {

		// 创建Spring上下文
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(SpringRootConfig.class);
		ConfigurableEnvironment environment = rootContext.getEnvironment();

		try {
			environment.getPropertySources().addFirst(
					new ResourcePropertySource(
							"classpath:/environment.properties"));
			environment.getPropertySources().addFirst(
					new ResourcePropertySource(
							"classpath:/serverHost.properties"));
		} catch (IOException e) {
			logger.error("", e);
		}
		// 管理Spring上下文的生命周期

		servletContext.addListener(new ContextLoaderListener(rootContext));
		// 将静态资源文件域名放入servlet上下文
		servletContext.setAttribute("staticFilesHost",
				environment.getProperty("static.files.host"));
		// CAS登出监听器
		// servletContext.addListener(new SingleSignOutHttpSessionListener());

		// 字符编码过滤器
		FilterRegistration.Dynamic characterEncodingFilter = servletContext
				.addFilter("characterEncodingFilter",
						new CharacterEncodingFilter());
		characterEncodingFilter.addMappingForUrlPatterns(
				EnumSet.allOf(DispatcherType.class), false, "/*");
		characterEncodingFilter.setInitParameter("encoding", "UTF-8");
		characterEncodingFilter.setInitParameter("forceEncoding", "true");

		// 该过滤器用于实现单点登出功能，可选配置。
		// FilterRegistration.Dynamic casSingleSignOutFiltert =
		// servletContext.addFilter("CAS Single Sign Out Filter",new
		// SingleSignOutFilter());
		// casSingleSignOutFiltert.addMappingForUrlPatterns(null, false, "/*");
		// 该过滤器负责用户的认证工作，必须启用它
		// FilterRegistration.Dynamic casAuthenticationFilter = servletContext
		// .addFilter("CAS Authentication Filter", new AuthenticationFilter());
		Map<String, String> casAuthenticationFilterParams = new HashMap<String, String>();
		casAuthenticationFilterParams.put("casServerLoginUrl",
				environment.getProperty("casServerLoginUrl"));
		casAuthenticationFilterParams.put("serverName",
				environment.getProperty("serverName"));
		// casAuthenticationFilter.setInitParameters(casAuthenticationFilterParams);
		// casAuthenticationFilter.addMappingForUrlPatterns(null, false,
		// "*.htm");
		// 该过滤器负责对Ticket的校验工作，必须启用它
		// FilterRegistration.Dynamic casValidationFilter =
		// servletContext.addFilter("CAS Validation Filter",new
		// Cas20ProxyReceivingTicketValidationFilter());
		Map<String, String> casValidationFilterParams = new HashMap<String, String>();
		casValidationFilterParams.put("casServerUrlPrefix",
				environment.getProperty("casServerUrlPrefix"));
		casValidationFilterParams.put("serverName",
				environment.getProperty("serverName"));
		// casValidationFilter.setInitParameters(casValidationFilterParams);
		// casValidationFilter.addMappingForUrlPatterns(null, false, "*.htm");
		//
		// FilterRegistration.Dynamic casWrapperFilter =
		// servletContext.addFilter(
		// "CAS HttpServletRequest Wrapper Filter",
		// new HttpServletRequestWrapperFilter());
		// casWrapperFilter.addMappingForUrlPatterns(null, false, "*.htm");
		// 该过滤器使得开发者可以通过org.jasig.cas.client.util.AssertionHolder来获取用户的登录名。
		// 比如AssertionHolder.getAssertion().getPrincipal().getName()
		// FilterRegistration.Dynamic casAssertionFilter = servletContext
		// .addFilter("CAS Assertion Thread Local Filter",
		// new AssertionThreadLocalFilter());
		// casAssertionFilter.addMappingForUrlPatterns(null, false, "*.htm");

		// 创建SpringMVC上下文
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.register(SpringMVCConfig.class);

		// 注册SpringMVC分发器
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				"dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
	}
}
