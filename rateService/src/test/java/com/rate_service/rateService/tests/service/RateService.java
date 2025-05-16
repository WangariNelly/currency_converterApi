package com.rate_service.rateService.tests.service;

import com.rate_service.rateService.tests.ExchangeRateClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RateService {
    private final ExchangeRateClient exchangeRateClient;

    public RateService(ExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    public BigDecimal getRate(String from, String to) {
        return exchangeRateClient.getRate(from, to);
    }
}
