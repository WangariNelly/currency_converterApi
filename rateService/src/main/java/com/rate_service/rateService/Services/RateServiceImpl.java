package com.rate_service.rateService.Services;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rate_service.rateService.Handlers.ExchangeRateException;
import com.rate_service.rateService.Interfaces.RateService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RateServiceImpl implements RateService {
    private final WebClient webClient;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(RateServiceImpl.class);

    @Autowired
    public RateServiceImpl(WebClient webClient,
                           @Value("${exchange.api.key}") String apiKey, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.objectMapper = objectMapper;
        if (apiKey == null || apiKey.isEmpty()) {
            logger.error("API key is not configured");
            throw new IllegalArgumentException("API key is not configured");
        }
        logger.info("Initialized RateService with API key: {}", apiKey.substring(0, 4) + "****");
    }

    public Mono<ObjectNode> getExchangeRate(String from, String to) {
        String normalizedFrom = from.toUpperCase();
        String normalizedTo = to.toUpperCase();

        System.out.println("DEBUG - Making API call for " + normalizedFrom + " to " + normalizedTo);
        logger.debug("Fetching exchange rate from {} to {}", normalizedFrom, normalizedTo);

        return webClient.get()
                .uri("/{apiKey}/latest/{from}", apiKey, normalizedFrom)
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    logger.error("API client error: {}", body);
                                    return Mono.error(new ExchangeRateException(
                                            "Failed to fetch rates: " + body));
                                }))
                .onStatus(status -> status.is5xxServerError(),
                        response -> {
                            logger.error("API server error");
                            return Mono.error(new ExchangeRateException(
                                    "Exchange rate service unavailable"));
                        })
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    logger.debug("Received response: {}", json);
                    // Validate response
                    if ("error".equals(json.path("result").asText())) {
                        throw new ExchangeRateException(
                                json.path("error-type").asText("API request failed")
                        );
                    }

                    if (!json.has("conversion_rates") ||
                            !json.get("conversion_rates").has(normalizedTo)) {
                        throw new ExchangeRateException(
                                "Target currency not found in response"
                        );
                    }

                    // Extract rates
                    ObjectNode ratesNode = objectMapper.createObjectNode();

                    logger.info("Exchange rate from {} to {}: {}",
                            normalizedFrom, normalizedTo,
                            json.get("conversion_rates").get(normalizedTo).asDouble());
                    System.out.println("Exchange rate from " + normalizedFrom + " to " + normalizedTo + ": " +
                            json.get("conversion_rates").get(normalizedTo).asDouble());
                    ratesNode.put("base", normalizedFrom);
                    ratesNode.put("target", normalizedTo);
                    ratesNode.put("rate", json.get("conversion_rates").get(normalizedTo).asDouble());
                    ratesNode.put("last_updated", json.get("time_last_update_utc").asText());
                    ratesNode.set("full_response", json);

                    return ratesNode;
                })
                .onErrorResume(e -> {
                    logger.error("Failed to fetch exchange rate", e);

                    ObjectNode errorResponse = objectMapper.createObjectNode();
                    errorResponse.put("error", e.getMessage());
                    errorResponse.put("from", normalizedFrom);
                    errorResponse.put("to", normalizedTo);
                    errorResponse.put("rate", 0.0);
                    errorResponse.put("last_updated", "");
                    return Mono.just( errorResponse);
                });
    }
}
