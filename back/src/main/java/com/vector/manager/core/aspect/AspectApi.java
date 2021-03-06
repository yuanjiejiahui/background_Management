package com.vector.manager.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public interface AspectApi {

    Object doHandlerAspect(ProceedingJoinPoint pjp, Method method)throws Throwable;
    
}
