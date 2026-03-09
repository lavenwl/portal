package com.example.rbac.log.aop;

import com.example.rbac.common.ApiResponse;
import com.example.rbac.log.OperationLogService;
import com.example.rbac.log.entity.OperationLogEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    public OperationLogAspect(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Around("@annotation(com.example.rbac.log.aop.OperationLog)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        OperationLog operationLog = method.getAnnotation(OperationLog.class);

        OperationLogEntity entity = new OperationLogEntity();
        entity.setModule(operationLog.module());
        entity.setAction(operationLog.action());
        entity.setRequestParams(trimToLength(Arrays.toString(joinPoint.getArgs()), 4000));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            entity.setUsername(authentication.getName());
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            entity.setRequestPath(request.getRequestURI());
            entity.setRequestMethod(request.getMethod());
            String traceId = request.getHeader("X-Trace-Id");
            if (traceId != null && !traceId.trim().isEmpty()) {
                entity.setTraceId(trimToLength(traceId.trim(), 64));
            }
        }

        try {
            Object result = joinPoint.proceed();
            entity.setSuccess(1);
            if (result instanceof ApiResponse) {
                ApiResponse<?> apiResponse = (ApiResponse<?>) result;
                entity.setResponseCode(String.valueOf(apiResponse.getCode()));
            } else {
                entity.setResponseCode("0");
            }
            return result;
        } catch (Throwable ex) {
            entity.setSuccess(0);
            entity.setResponseCode("EXCEPTION");
            entity.setErrorMessage(trimToLength(ex.getMessage(), 255));
            throw ex;
        } finally {
            entity.setDurationMs(System.currentTimeMillis() - start);
            operationLogService.save(entity);
        }
    }

    private String trimToLength(String value, int length) {
        if (value == null) {
            return null;
        }
        return value.length() <= length ? value : value.substring(0, length);
    }
}
