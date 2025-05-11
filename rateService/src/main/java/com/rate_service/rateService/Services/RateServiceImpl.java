package com.rate_service.rateService.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.rate_service.rateService.Dtos.RateResponseDto;
import com.rate_service.rateService.Interfaces.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RateServiceImpl implements RateService {
    private final WebClient webClient;

    @Value("${exchange.api.key}")
    private String apiKey;

    @Autowired
    public RateServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    public RateResponseDto getExchangeRate(String from, String to) {
        String apiUrl = String.format(
                "https://v6.exchangerate-api.com/v6/%s/latest/%s",
                apiKey,
                from.toUpperCase()
        );

        System.out.println("API URL: " + apiUrl);
        JsonNode response = webClient.get()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Accept", "application/json")
                .retrieve()
                .onStatus(
                        status -> !status.is2xxSuccessful(),
                        clientResponse -> Mono.error(new RuntimeException("API call failed: " + clientResponse.statusCode()))
                )
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("rates")) {
            throw new RuntimeException("Failed to fetch exchange rates");
        }

        double rate = response.get("rates").get(to.toUpperCase()).asDouble();
      return new RateResponseDto(from, to, rate);
    }
}
