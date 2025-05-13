package com.rate_service.rateService.Interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

public interface RateService {
   Mono<JsonNode> getExchangeRate(String from, String to);
}
