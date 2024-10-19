package org.example.aop;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.types.annotation.DCCValue;
import org.example.types.annotation.RateLimitAccessInterceptor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class RateLimitAOP {

    @DCCValue("rateLimitSwitch:close")
    private String rateLimitSwitch;

    private final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();

    private final Cache<String, Long> blackList = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();


    @Pointcut("@annotation(org.example.types.annotation.RateLimitAccessInterceptor)")
    public void aopNode(){

    }


    @Around("aopNode() && @annotation(rateLimitAccessInterceptor)")
    public Object doRouter(ProceedingJoinPoint proceedingJoinPoint, RateLimitAccessInterceptor rateLimitAccessInterceptor) throws Throwable {
        // check rateLimitSwitchStatus
        if (!StringUtils.isBlank(rateLimitSwitch) && "close".equals(rateLimitSwitch)){
            return proceedingJoinPoint.proceed();
        }

        // get key first
        String key = rateLimitAccessInterceptor.key();
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("uId is null");
        }

        String keyValue = getAttrValue(key, proceedingJoinPoint.getArgs());
        log.info("aop attribution: {}", keyValue);

        // in the blacklist, catch it and return
        if (!"all".equals(keyValue) && rateLimitAccessInterceptor.blackListCount() != 0 && null != blackList.getIfPresent(keyValue) && blackList.getIfPresent(keyValue) >= rateLimitAccessInterceptor.blackListCount()) {
            log.info("rate limit - reach intercept bar: {}", keyValue);
            return feedbackMethodResult(proceedingJoinPoint, rateLimitAccessInterceptor.feedbackMethod());
        }

        RateLimiter rateLimiter = loginRecord.getIfPresent(keyValue);
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(rateLimitAccessInterceptor.permitPerSecond());
            loginRecord.put(keyValue,rateLimiter);
        }

        // not in blacklist, increase access count for current user
        if (rateLimiter.tryAcquire()) {
            if (rateLimitAccessInterceptor.blackListCount() != 0 && blackList.getIfPresent(keyValue) == null){
                blackList.put(keyValue,1L);
            } else {
                blackList.put(keyValue, blackList.getIfPresent(keyValue) + 1L);
            }
            log.info("rate limit - user: {}, access count: {}", keyValue, blackList.getIfPresent(keyValue));
//            return feedbackMethodResult(proceedingJoinPoint, rateLimitAccessInterceptor.feedbackMethod());
        }


        return proceedingJoinPoint.proceed();
    }

    /**
     * call customize method after intercept
     */
    private Object feedbackMethodResult(JoinPoint jp, String feedbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Signature sig = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) sig;
        Method method = jp.getTarget().getClass().getMethod(feedbackMethod, methodSignature.getParameterTypes());
        return method.invoke(jp.getThis(), jp.getArgs());
    }


    /**
     * Adjust according to actual business needs, mainly to intercept based on a certain value.
     */
    public String getAttrValue(String attr, Object[] args) {
        if (args[0] instanceof String) {
            return args[0].toString();
        }
        String filedValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                // filedValue = BeanUtils.getProperty(arg, attr);
                // fix: When using Lombok, the `get` method for fields like uId is different from the method generated by IDEA,
                // which can lead to not being able to retrieve the property value. This is fixed by using reflection to obtain the value.
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                log.error("get attribution fail, attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * Get the specific attribute value of an object
     *
     * @param item Object
     * @param name Attribute name
     * @return Attribute value
     * @author tang
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Get the field by name, this method also considers retrieving attributes from a superclass in case of inheritance
     *
     * @param item Object
     * @param name Attribute name
     * @return The field corresponding to the attribute name
     * @author tang
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
