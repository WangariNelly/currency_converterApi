package com.rate_service.rateService.Controllers;

import com.rate_service.rateService.Dtos.RateResponseDto;
import com.rate_service.rateService.Interfaces.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rate")
public class RateController {
  private final RateService rateService;

  @Autowired
  public RateController(RateService rateService) {
    this.rateService = rateService;
  }

  @GetMapping("/exchange")
  public ResponseEntity<RateResponseDto> gtExchangeRate(
          @RequestParam String from,
          @RequestParam String to) {
    try {
      System.out.println("Fetching exchange rate from " + from + " to " + to);
      return ResponseEntity.ok(rateService.getExchangeRate(from, to));
    } catch (Exception e) {
      e.printStackTrace();
     return ResponseEntity.badRequest().body(new RateResponseDto(from, to, 0));
    }
  }

  @GetMapping("/status")
    public ResponseEntity<Map<String, String >> status() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
