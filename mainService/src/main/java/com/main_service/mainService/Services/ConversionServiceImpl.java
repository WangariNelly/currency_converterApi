package com.main_service.mainService.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;
import com.main_service.mainService.Dtos.RateResponseDTO;
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
    public JsonNode convertCurrency(String from, String to, double amount) {
        if (from == null || to == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        String normalizedFrom = from.toUpperCase();
        String normalizedTo = to.toUpperCase();

        try {
            JsonNode json = webClientBuilder.build().get()

                    .uri(rateServiceUrl + "/convert?from={from}&to={to}", normalizedFrom, normalizedTo)
                    .header("X-API-KEY", rateServiceApiKey)
                    .retrieve()
                    .onStatus(code -> !code.is2xxSuccessful(), response ->
                            Mono.error(new RuntimeException("Rate service failed: " + response.statusCode()))
                    )
                    .bodyToMono(JsonNode.class)
                    .block();

            if (json == null || !json.has("rate")) {
                throw new RuntimeException("Invalid response from rate service");
            }

            double rate = json.get("conversion_rates").get(normalizedTo).asDouble();
            String lastUpdated = json.path("time_last_update_utc").asText();
            double convertedAmount = rate * amount;

            logger.info("Exchange rate from {} to {}: {}", normalizedFrom, normalizedTo, rate);

            ObjectNode response = JsonNodeFactory.instance.objectNode();
            response.put("base", normalizedFrom);
            response.put("target", normalizedTo);
            response.put("rate", rate);
            response.put("last_updated", lastUpdated);
            response.put("amount", amount);
            response.put("converted_amount", convertedAmount);
            response.set("full_response", json);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Currency Conversion failed: " + e.getMessage());
        }
    }
}
