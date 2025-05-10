package com.rate_service.rateService.Interfaces;

import com.rate_service.rateService.Dtos.RateResponseDto;

public interface RateService {
    RateResponseDto getExchangeRate(String from, String to);
}
