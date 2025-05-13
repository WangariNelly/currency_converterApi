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

    public Mono<JsonNode> getExchangeRate(String from, String to) {
        String normalizedFrom = from.toUpperCase();
        String normalizedTo = to.toUpperCase();

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

                    double rate = json.get("conversion_rates").get(normalizedTo).asDouble();
                    String lastUpdated = json.path("time_last_update_utc").asText();

                    logger.info("Exchange rate from {} to {}: {}", normalizedFrom, normalizedTo, rate);

                    String jsonString = String.format(
                            "{" +
                                    "\"base\":\"%s\"," +
                                    "\"target\":\"%s\"," +
                                    "\"rate\":%f," +
                                    "\"last_updated\":\"%s\"," +
                                    "\"full_response\":%s" +
                                    "}",
                            normalizedFrom,
                            normalizedTo,
                            rate,
                            lastUpdated,
                            json.toString()
                    );

                    try {
                        return new ObjectMapper().readTree(jsonString);
                    } catch (Exception e) {
                        throw new ExchangeRateException("Failed to parse constructed JSON: " + e.getMessage());
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Failed to fetch exchange rate", e);

                    String fallbackJson = String.format(
                            "{" +
                                    "\"error\":\"%s\"," +
                                    "\"from\":\"%s\"," +
                                    "\"to\":\"%s\"," +
                                    "\"rate\":0.0," +
                                    "\"last_updated\":\"\"" +
                                    "}", e.getMessage(), normalizedFrom, normalizedTo);

                    try {
                        return Mono.just(new ObjectMapper().readTree(fallbackJson));
                    } catch (Exception ex) {
                        logger.error("Failed to create fallback JSON", ex);
                        return Mono.error(new RuntimeException("Failed to create fallback error JSON"));
                    }
                });
    }

}
