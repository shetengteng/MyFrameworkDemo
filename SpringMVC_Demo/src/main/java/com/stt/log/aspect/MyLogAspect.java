package com.stt.log.aspect;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import com.stt.log.annotation.MyLog;

@Aspect
public class MyLogAspect {

	// 如果要将log信息存入数据库，或者redis中，或者发送mq消息
	// 那么首先要添加@Component，然后注入相应的数据库操作对象以及redis对象

	public MyLogAspect() {
		System.out.println("-------MyLogAspect");
	}

	// 定义切点
	@Pointcut("@annotation(com.stt.log.annotation.MyLog)")
	public void myLogAnnotationPointcut() {

	}

	@Around("myLogAnnotationPointcut()")
	public Object around(ProceedingJoinPoint joinPoint) {
		// 进行日志操作
		try {
			// 获取描述
			String description = getDescription(joinPoint);

			String methodName = joinPoint.getSignature().getName();
			Object[] args = joinPoint.getArgs();
			System.out.println(description + "------method:" + methodName
					+ " args:" + Arrays.asList(args).toString());
			// 执行目标的方法
			Object result = joinPoint.proceed();
			System.out.println(description + "-----return: " + result);
			return result;
		} catch (Throwable e) {
			System.out.println("-----ERROR: " + e);
		}
		return null;
	}

	/**
	 * 注解中的获取描述信息
	 * 
	 * @param joinPoint
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private static String getDescription(ProceedingJoinPoint joinPoint)
			throws NoSuchMethodException, SecurityException {

		// 获取目标对象的类名
		Class targetClass = joinPoint.getTarget().getClass();

		// 获取方法的签名
		Signature signature = joinPoint.getSignature();
		if (!(signature instanceof MethodSignature)) {
			throw new IllegalArgumentException("该注解只能用在方法上，而非接口的方法声明上");
		}

		MethodSignature methodSignature = (MethodSignature) signature;

		// 获取当前方法，也可以通过遍历来获取， 不过速度很慢，而且容易错，特别是重载的方法，不好判断
		Method currentMethod = targetClass.getMethod(methodSignature.getName(),
				methodSignature.getParameterTypes());

		MyLog myLog = currentMethod.getAnnotation(MyLog.class);
		if (myLog == null) {
			throw new IllegalArgumentException("日志注解为空");
		}
		return myLog.description();
	}
}
