package com.rate_service.rateService.tests.service;

import com.rate_service.rateService.tests.ExchangeRateClient;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class RateServiceTest {
    @Mock
    private ExchangeRateClient exchangeRateClient;

    @InjectMocks
    private RateService rateService;

    @Test
    void shouldReturnRateFromClient() {
        String from = "USD";
        String to = "KES";
        BigDecimal mockRate = new BigDecimal("129.90");

        when(exchangeRateClient.getRate(from, to)).thenReturn(mockRate);

        BigDecimal result = rateService.getRate(from, to);
        System.out.println(result);

        assertEquals(mockRate, result);
        verify(exchangeRateClient).getRate(from, to);
    }

}
