package com.rate_service.rateService.Interfaces;

import com.fasterxml.jackson.databind.node.ObjectNode;
import reactor.core.publisher.Mono;

public interface RateService {
   Mono<ObjectNode> getExchangeRate(String from, String to);
}
