package com.rate_service.rateService.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rate_service.rateService.Interfaces.RateService;
import com.rate_service.rateService.Services.RateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rate")
public class RateController {

  private final RateService rateService;
  private final RateServiceImpl rateServiceImpl;

  @Autowired
  public RateController(RateService rateService, RateServiceImpl rateServiceImpl) {
    this.rateService = rateService;
    this.rateServiceImpl = rateServiceImpl;
  }

  @GetMapping("/convert")
  public Mono<JsonNode> convertCurrency(
          @RequestParam String from,
          @RequestParam String to) {

    System.out.println("Fetching exchange rate from " + from + " to " + to);

    if (isValidCurrencyCode(from) && isValidCurrencyCode(to)) {
      return rateServiceImpl.getExchangeRate(from.toUpperCase(), to.toUpperCase());
    }

    ObjectNode error = JsonNodeFactory.instance.objectNode();
    error.put("error", "Invalid currency code(s)");
    error.put("from", from);
    error.put("to", to);
    error.put("rate", 0.0);
    error.put("last_updated", "");
    return Mono.just(error);
  }

  @GetMapping("/status")
  public ResponseEntity<Map<String, String>> status() {
    return ResponseEntity.ok(Map.of("status", "UP"));
  }

  private boolean isValidCurrencyCode(String code) {
    return code != null && code.matches("^[A-Z]{3}$");
  }
}
