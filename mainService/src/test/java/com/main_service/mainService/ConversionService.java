package com.main_service.mainService;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConversionService {

    public BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate);
    }
}
