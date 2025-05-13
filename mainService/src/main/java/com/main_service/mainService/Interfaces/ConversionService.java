package com.main_service.mainService.Interfaces;

import com.fasterxml.jackson.databind.JsonNode;
import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;

public interface ConversionService {
    JsonNode convertCurrency(String from,String to, double amount);
}
