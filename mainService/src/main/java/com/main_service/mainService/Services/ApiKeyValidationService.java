package com.main_service.mainService.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyValidationService {

    @Value("${app.api.key}")
    private String validApiKey;

    @Value("${security.api.enabled:true}")
    private boolean apiKeyEnabled;

    public boolean isValidApiKey(String apiKey) {
        if (!apiKeyEnabled) {
            return true;
        }
        return validApiKey.equals(apiKey);
    }
}
