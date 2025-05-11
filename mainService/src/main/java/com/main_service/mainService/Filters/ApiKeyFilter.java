package com.main_service.mainService.Filters;

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

    @Value("${app.api.key}") private String validApiKey;
    @Value("${app.api.secret}") private String validApiSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {


        logger.info("Request - method: {}, URI: {}, IP: {}",
                req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
        String apiKey = req.getHeader("X-API-KEY");
        String apiSecret = req.getHeader("X-API-SECRET");

        logger.info("API Key: {}", apiKey);

        if (!isValidApiKey(apiKey, apiSecret)) {
            logger.warn("Invalid API Key attempt from IP: {}", req.getRemoteAddr());
            throw new AuthenticationCredentialsNotFoundException("Invalid API credentials");
        }

        chain.doFilter(req, res);
    }

    private boolean isValidApiKey(String apiKey, String apiSecret) {
        return validApiKey.equals(apiKey) && validApiSecret.equals(apiSecret);
    }

    public String getValidApiSecret() {
        return validApiSecret;
    }

    public void setValidApiSecret(String validApiSecret) {
        this.validApiSecret = validApiSecret;
    }

    public String getValidApiKey() {
        return validApiKey;
    }

    public void setValidApiKey(String validApiKey) {
        this.validApiKey = validApiKey;
    }
}
