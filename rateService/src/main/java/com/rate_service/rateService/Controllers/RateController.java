package com.rate_service.rateService.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rate_service.rateService.Interfaces.RateService;
import com.rate_service.rateService.Services.RateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

//  @GetMapping("/exchange")
//  public ResponseEntity<RateResponseDto> gtExchangeRate(
//          @RequestParam String from,
//          @RequestParam String to) {
//    try {
//      System.out.println("Fetching exchange rate from " + from + " to " + to);
//      return ResponseEntity.ok(rateService.getExchangeRate(from, to));
//    } catch (Exception e) {
//      e.printStackTrace();
//     return ResponseEntity.badRequest().body(new RateResponseDto(from, to, 0));
//    }
//  }

  @GetMapping("/convert")
  public Mono<?> convertCurrency(
          @RequestParam String from,
          @RequestParam String to) {
    System.out.println("Fetching exchange rate from " + from + " to " + to);
    if (!isValidCurrencyCode(from) || !isValidCurrencyCode(to)) {
      return rateServiceImpl.getExchangeRate(from, to);
    }
    return null;
  }

  @GetMapping("/status")
    public ResponseEntity<Map<String, String >> status() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

  private boolean isValidCurrencyCode(String code) {
    return code != null && code.matches("^[A-Z]{3}$");
  }
}
