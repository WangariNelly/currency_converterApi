package com.rate_service.rateService.Services;

import com.fasterxml.jackson.databind.JsonNode;
import com.rate_service.rateService.Dtos.RateResponseDto;
import com.rate_service.rateService.Interfaces.RateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RateServiceImpl implements RateService {
    private final WebClient webClient;

    @Autowired
    public RateServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }
    public RateResponseDto getExchangeRate(String from, String to) {
        JsonNode response = webClient.get()
                .uri(from.toUpperCase())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (response == null || !response.has("rates")) {
            throw new RuntimeException("Failed to fetch exchange rates");
        }

        double rate = response.get("rates").get(to.toUpperCase()).asDouble();
      return new RateResponseDto(from, to, rate);
    }
}
