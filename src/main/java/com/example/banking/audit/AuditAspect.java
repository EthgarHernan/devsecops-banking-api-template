package com.example.banking.audit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class AuditAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Around("within(com.example.banking.controller..*) || within(com.example.banking.service..*)")
    public Object auditOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String operation = joinPoint.getSignature().toShortString();
        Instant timestamp = Instant.now();
        try {
            Object result = joinPoint.proceed();
            log.info("operation={} timestamp={} outcome=SUCCESS", operation, timestamp);
            return result;
        } catch (Throwable ex) {
            log.warn("operation={} timestamp={} outcome=ERROR errorType={}",
                    operation,
                    timestamp,
                    ex.getClass().getSimpleName());
            throw ex;
        }
    }
}

