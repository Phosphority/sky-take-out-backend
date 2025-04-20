package com.sky.advice;


import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint joinPoint) {

        // NOTICE:注意这个切入的写法
        // 1.获取切入点的方法与切入点的注解以及注解参数
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();

        // 2.获取切入点的参数,以及准备好要通过一设置的参数
        Object[] args = joinPoint.getArgs();
        // 3.判断方法参数是否为空
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];
        long currentId = BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        // 4.使用反射获取参数的set方法,以及set默认插入的值
        try {
            Method setCreteTime = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, currentId);
            // 5.判断是否设置create相关的值
            if (value == OperationType.INSERT) {
                setCreteTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}




















