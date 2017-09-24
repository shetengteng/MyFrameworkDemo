package com.stt.quickstart;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Quickstart application showing how to use Shiro's API. helloworld
 * 版本，开发时不用
 * 
 * @since 0.9 RC2
 */
public class Quickstart {

    private static final transient Logger log = LoggerFactory
            .getLogger(Quickstart.class);

    public static void main(String[] args) {

        // The easiest way to create a Shiro SecurityManager with configured
        // realms, users, roles and permissions is to use the simple INI config.
        // We'll do that by using a factory that can ingest a .ini file and
        // return a SecurityManager instance:

        // Use the shiro.ini file at the root of the classpath
        // (file: and url: prefixes load from files and urls respectively):

        // 通过shiro.ini文件配置了realms users 以及roles
        // 通过工厂模式读取该ini文件获取SecurityManager实例
        Factory<SecurityManager> factory = new IniSecurityManagerFactory(
                "classpath:shiro.ini");
        SecurityManager securityManager = factory.getInstance();

        // for this simple example quickstart, make the SecurityManager
        // accessible as a JVM singleton. Most applications wouldn't do this
        // and instead rely on their container configuration or web.xml for
        // webapps. That is outside the scope of this simple quickstart, so
        // we'll just do the bare minimum so you can continue to get a feel
        // for things.

        // 实际使用中不这么做
        SecurityUtils.setSecurityManager(securityManager);

        // 重要：获取当前的subject 一般从数据库关联
        Subject currentUser = SecurityUtils.getSubject();

        // Do some stuff with a Session (no need for a web or EJB container!!!)
        // 测试session的使用

        // 获取session
        Session session = currentUser.getSession();
        // session中设置一个key和value
        session.setAttribute("someKey", "aValue");
        // 获取该key和value
        String value = (String) session.getAttribute("someKey");
        // 比较该key和value 该key的value值和外部的一样则通过
        if (value.equals("aValue")) {
            log.info("------>Retrieved the correct value! [" + value + "]");
        }

        // 测试当前用户是否已经通过验证，即是否已经登录
        // 调用subject的验证功能
        if (!currentUser.isAuthenticated()) {

            // 没有验证则 将验证名称和密码封装为token对象

            UsernamePasswordToken token = new UsernamePasswordToken(
                    "lonestarr", "vespa");
            token.setRememberMe(true);
            try {
                // 执行登录操作
                currentUser.login(token);

            } catch (UnknownAccountException uae) {
                // 若用户名不存在，则抛出该异常
                log.info("There is no user with username of "
                        + token.getPrincipal());

            } catch (IncorrectCredentialsException ice) {
                // 账户存在，但是密码错误时，抛出该异常
                log.info("Password for account " + token.getPrincipal()
                        + " was incorrect!");

            } catch (LockedAccountException lae) {
                // 用户被锁定的异常
                log.info("The account for username " + token.getPrincipal()
                        + " is locked.  "
                        + "Please contact your administrator to unlock it.");
            } catch (AuthenticationException ae) {
                // unexpected condition? error?
                // 其他认证异常，可以使用ctrl+t查看异常子类
            }
        }

        log.info("####################登录成功----->User ["
                + currentUser.getPrincipal() + "] logged in successfully.");

        // 测试subject是否含有角色
        if (currentUser.hasRole("schwartz")) {
            log.info("May the Schwartz be with you!");
        } else {
            log.info("Hello, mere mortal.");
        }

        // test a typed permission (not instance-level)
        // 角色是否有某些行为，权限动作

        // 在ini配置文件中lightsaber:*，因此后面任意单词都可以
        if (currentUser.isPermitted("lightsaber:weildsssss")) {
            log.info("You may use a lightsaber ring.  Use it wisely.");
        } else {
            log.info("Sorry, lightsaber rings are for schwartz masters only.");
        }

        // a (very powerful) Instance Level permission:

        // 该三个单词有具体的语义，
        // #对user这个类型的对象zhangsan进行删除操作的角色
        // myrole = user:delete:zhangsan
        // 首先是类型，中间的是行为，最后的是id
        if (currentUser.isPermitted("winnebago:drive:eagle5")) {
            log.info("You are permitted to 'drive' the winnebago with license plate (id) 'eagle5'.  "
                    + "Here are the keys - have fun!");
        } else {
            log.info("Sorry, you aren't allowed to drive the 'eagle5' winnebago!");
        }
        System.out.println("未登出，是否被认证：" + currentUser.isAuthenticated());
        // 执行登出操作
        currentUser.logout();

        System.out.println("已登出，是否被认证：" + currentUser.isAuthenticated());
        System.exit(0);
    }
}
