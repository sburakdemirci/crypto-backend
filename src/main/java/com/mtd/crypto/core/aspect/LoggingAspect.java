package com.mtd.crypto.core.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;


@Aspect
@Component
public class LoggingAspect {


    @Around("@within(com.mtd.crypto.core.aspect.LoggableClass)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        // Get class name, method name, and arguments
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
         Logger logger = LoggerFactory.getLogger(LoggingAspect.class);


        // Start the stopwatch
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Log method invocation

        // Log method invocation

        // Proceed with the method execution
        Object result = joinPoint.proceed();

        // Stop the stopwatch
        stopWatch.stop();

        // Log execution time

            logger.info("----METHOD LOGGING STARTED-----");
            logger.info("Class: {}", className);
            logger.info("Method: {}", methodName);
            logger.info("Parameters: {}", Arrays.toString(arguments));
            logger.info("Execution Time: {} ms", stopWatch.getTotalTimeMillis());
            logger.info("Result: {}", result);
            logger.info("------------");



        return result;
    }

}




