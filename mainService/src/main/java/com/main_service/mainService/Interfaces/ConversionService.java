package com.main_service.mainService.Interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;
import reactor.core.publisher.Mono;

public interface ConversionService {
   Mono<JsonNode> convertCurrency(String from, String to, double amount);
}
