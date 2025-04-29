package com.academy.taskService.Aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Before("@annotation(com.academy.taskService.Aspect.Annotation.Loggable)")
    public void logExecution(JoinPoint joinPoint) {
        log.info("Method {} was called", joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "@annotation(com.academy.taskService.Aspect.Annotation.ExceptionHandling)", throwing = "ex")
    public void logException(JoinPoint joinPoint, Exception ex) {
        log.error("Exception {} in method: {}", ex.getMessage(),
                joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "@annotation(com.academy.taskService.Aspect.Annotation.LogUpdate)", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.error("Method {} was called with result: {}",joinPoint.getSignature().getName(), result );
    }

    @Around("@annotation(com.academy.taskService.Aspect.Annotation.LogTracking)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceede = joinPoint.proceed();
        long stopTime = System.currentTimeMillis();

        log.info("Execution time: " + (stopTime - startTime));
        return proceede;
    }

}
