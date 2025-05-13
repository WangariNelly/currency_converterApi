package com.main_service.mainService.Filters;

import com.main_service.mainService.Services.ApiKeyValidationService;
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
    private static final String API_KEY_HEADER = "X-API-KEY";

    private final ApiKeyValidationService validationService;

    public ApiKeyFilter(ApiKeyValidationService validationService) {
        this.validationService = validationService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // Skip filter for public endpoints
        if (isPublicEndpoint(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey == null || apiKey.isBlank()) {
            logger.warn("Missing API key for request to {}", requestUri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "API key is required");
            return;
        }

        if (!validationService.isValidApiKey(apiKey)) {
            logger.warn("Invalid API key provided for request to {}", requestUri);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid API key");
            return;
        }

        logger.debug("Valid API key provided for {}", requestUri);
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestUri) {
        return requestUri.startsWith("/actuator") ||
                requestUri.startsWith("/api/v1/status") ||
                requestUri.startsWith("/v3/api-docs") ||
                requestUri.startsWith("/swagger-ui");

    }
//    private static final Logger logger = LoggerFactory.getLogger(ApiKeyFilter.class);
//    private static final String API_KEY_HEADER = "X-API-KEY";
//    @Value("${app.api.key}") private String validApiKey;
//    @Value("${app.api.secret}") private String validApiSecret;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
//
//
//        logger.info("Request - method: {}, URI: {}, IP: {}",
//                req.getMethod(), req.getRequestURI(), req.getRemoteAddr());
//        String apiKey = req.getHeader("X-API-KEY");
//        String apiSecret = req.getHeader("X-API-SECRET");
//
//        logger.info("API Key: {}", apiKey);
//
//        if (!isValidApiKey(apiKey, apiSecret)) {
//            logger.warn("Invalid API Key attempt from IP: {}", req.getRemoteAddr());
//            throw new AuthenticationCredentialsNotFoundException("Invalid API credentials");
//        }
//
//        chain.doFilter(req, res);
//    }
//
//    private boolean isValidApiKey(String apiKey, String apiSecret) {
//        return validApiKey.equals(apiKey) && validApiSecret.equals(apiSecret);
//    }
//
//    public String getValidApiSecret() {
//        return validApiSecret;
//    }
//
//    public void setValidApiSecret(String validApiSecret) {
//        this.validApiSecret = validApiSecret;
//    }
//
//    public String getValidApiKey() {
//        return validApiKey;
//    }
//
//    public void setValidApiKey(String validApiKey) {
//        this.validApiKey = validApiKey;
//    }
}
