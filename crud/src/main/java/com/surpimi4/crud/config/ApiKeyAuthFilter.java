package com.surpimi4.crud.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${api.secret}")
    private String apiSecret;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/routing/api/") || requestURI.startsWith("/geocode/api/")) {
            String apiKey = request.getHeader("X-MY-API-KEY");

            if (apiKey == null || !apiKey.equals(apiSecret)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid api key");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
