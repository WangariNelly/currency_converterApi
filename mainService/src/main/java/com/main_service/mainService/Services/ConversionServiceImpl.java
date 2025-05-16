package com.main_service.mainService.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main_service.mainService.Interfaces.ConversionService;
import com.main_service.mainService.Models.Conversion;
import com.main_service.mainService.Repos.ConversionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {

    @Autowired
    private ConversionRepository conversionRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${rate.service.api.key}")
    private String rateServiceApiKey;

    @Value("${rate-service.url}")
    private String rateServiceUrl;

    private static final Logger logger = LoggerFactory.getLogger(ConversionServiceImpl.class);

    @Override
    public Mono<JsonNode> convertCurrency(String from, String to, double amount) {
        System.out.println("ConversionServiceImpl.convertCurrency!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

        if (from == null || to == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String normalizedFrom = from.toUpperCase();
        String normalizedTo = to.toUpperCase();
        logger.debug("Fetching exchange rate from {} to {}", normalizedFrom, normalizedTo);

        return webClientBuilder.build().get()
                .uri(rateServiceUrl + "/convert?from={from}&to={to}", normalizedFrom, normalizedTo)
                .header("X-API-KEY", rateServiceApiKey)
                .retrieve()
                .onStatus(code -> !code.is2xxSuccessful(), response ->
                        Mono.error(new RuntimeException("Rate service failed: " + response.statusCode()))
                )
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    logger.debug("Received response: {}", json);

                    if ("error".equals(json.path("result").asText())) {
                        throw new RuntimeException(json.path("error-type").asText("API request failed"));
                    }


                    BigDecimal rate;
// Try both possible formats
                    if (json.has("full_response") && json.get("full_response").has("conversion_rates")
                            && json.get("full_response").get("conversion_rates").has(normalizedTo)) {

                        rate = new BigDecimal(json.get("full_response")
                                .get("conversion_rates")
                                .get(normalizedTo)
                                .asText());

                    } else if (json.has("conversion_rate")) {
                        rate = new BigDecimal(json.get("conversion_rate").asText());
                    } else {
                        throw new RuntimeException("Missing conversion rate in response");
                    }

                    BigDecimal convertedAmount = rate.multiply(BigDecimal.valueOf(amount));
                    String lastUpdated = json.path("full_response").path("time_last_update_utc").asText();

                    logger.info("Exchange rate from {} to {}: {}", normalizedFrom, normalizedTo, rate);
                    logger.info("Converted amount: {}", convertedAmount);

                    //Prepare conversion object for db save
                    Conversion conversion = new Conversion(
                            normalizedFrom,
                            normalizedTo,
                            BigDecimal.valueOf(amount),
                            rate,
                            convertedAmount,
                            LocalDateTime.now()
                    );

                    conversionRepository.save(conversion);
                    logger.info("Saved To db...");
                    System.out.println("Loaded to Db");

                    String jsonString = String.format(
                            "{" +
                                    "\"base\":\"%s\"," +
                                    "\"target\":\"%s\"," +
                                    "\"rate\":%.4f," +
                                    "\"amount\":%.2f," +
                                    "\"last_updated\":\"%s\"" +
                                    "}",
                            normalizedFrom,
                            normalizedTo,
                            rate,
                            amount,
                            lastUpdated
                    );

                    logger.debug("Constructed JSON: {}", jsonString);

                    try {
                        return new ObjectMapper().readTree(jsonString);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse constructed JSON: " + e.getMessage());
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
