package com.surpimi4.crud.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ApiKeyAuthAspect {

    @Value("${api.secret}")
    private String apiSecret;

    @Around("@annotation(com.surpimi4.crud.annotation.Internal)")
    public Object validateApiKey(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String apiKey = request.getHeader("X-MY-API-KEY");

        if (apiKey == null || !apiKey.equals(apiSecret)) {
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return null;
        }

        return joinPoint.proceed();
    }
}