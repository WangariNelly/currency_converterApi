package com.rate_service.rateService.Filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiKeyFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);

    @Value("${security.api.key}") private String key;
    @Value("${security.api.secret}") private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {


        logger.info("Request - method: {}, URI: {}, IP: {}",
                req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
        String apiKey = req.getHeader("x-api-key");
        String apiSecret = req.getHeader("x-api-secret");

        logger.info("API Key: {}", apiKey);

        if (!isValidApiKey(apiKey, apiSecret)) {
            logger.warn("Invalid API Key attempt from IP: {}", req.getRemoteAddr());
            throw new AuthenticationCredentialsNotFoundException("Invalid API credentials");
        }

        chain.doFilter(req, res);
    }

    private boolean isValidApiKey(String apiKey, String apiSecret) {
        return key.equals(apiKey) && secret.equals(apiSecret);
    }
}
