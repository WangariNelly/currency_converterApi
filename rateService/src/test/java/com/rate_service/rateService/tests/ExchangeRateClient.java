package com.rate_service.rateService.tests;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ExchangeRateClient {
    public BigDecimal getRate(String from, String to) {
        throw new UnsupportedOperationException("This should be mocked in tests");
    }
}
