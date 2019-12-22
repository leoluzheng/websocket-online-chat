package com.wdg.chatonlinewebsocket.utils;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * ClassName: LogAspect
 * Description:
 * date: 2019/11/21 10:24
 *
 * @author WuDG(1490727316)
 * @version 0.1
 * @since JDK 1.8
 */
@Aspect
@Component
@SuppressWarnings("all")
public class LogAspect {

    private static final Logger LOG = LoggerFactory.getLogger(LogAspect.class);
    /**
     * 用来记录请求进入的时间，防止多线程时出错，这里用了ThreadLocal
     */
    ThreadLocal<Long> startTime = new ThreadLocal<>();
    /**
     * 定义切入点，controller下面的所有类的所有公有方法，这里需要更改成自己项目的
     */
    @Pointcut("execution(public * com.wdg.chatonlinewebsocket.controller..*.*(..))")
    public void requestLog(){};

    /**
     * 方法之前执行，日志打印请求信息
     * @param joinPoint joinPoint
     */
    @Before("requestLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        Object[] args = joinPoint.getArgs();
        Object[] arguments  = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
                //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                return;
            }
            arguments[i] = args[i];
        }

        //打印当前的请求路径
        LOG.info("------------------------------------------------");
        LOG.info("请求路径:[{}]",request.getRequestURI());

        /**
         * 打印请求参数，如果需要打印其他的信息可以到request中去拿
         */
        LOG.info("请求参数:{}", JSON.toJSONString(joinPoint.getArgs()==null?"":joinPoint.getArgs()));
    }

    /**
     * 方法返回之前执行，打印才返回值以及方法消耗时间
     * @param response 返回值
     */
    @AfterReturning(returning = "response",pointcut = "requestLog()")
    public void doAfterRunning(Object response) {
        //打印返回值信息
        LOG.info("返回数据:[{}]",JSON.toJSONString(response) );
        //打印请求耗时
        LOG.info("请求使用时间 : [{}ms]",System.currentTimeMillis()-startTime.get());
        LOG.info("------------------------------------------------");
    }
}