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

    @Value("${security.api.key}")
    private String key;

    @Value("${security.api.secret}")
    private String secret;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {

        logger.info("Request - method: {}, URI: {}, IP: {}",
                req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
        String apiKey = req.getHeader("x-api-key");
        String apiSecret = req.getHeader("x-api-secret");

        logger.info("API Key: {}", apiKey);
        logger.info("Expected key: {}, Provided key: {}", key, apiKey);
        logger.info("Expected secret: {}, Provided secret: {}", secret, apiSecret);
        if (apiKey == null || apiSecret == null) {
            logger.warn("Missing API Key or Secret from IP: {}", req.getRemoteAddr());
            throw new AuthenticationCredentialsNotFoundException("Missing API credentials");
        }
        if (key == null || secret == null) {
            logger.warn("API Key or Secret not configured");
            throw new AuthenticationCredentialsNotFoundException("API credentials not configured");
        }
        if (key.isEmpty() || secret.isEmpty()) {
            logger.warn("API Key or Secret is empty");
            throw new AuthenticationCredentialsNotFoundException("API credentials are empty");
        }

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
