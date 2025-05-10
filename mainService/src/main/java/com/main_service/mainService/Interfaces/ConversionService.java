package com.main_service.mainService.Interfaces;

import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;

public interface ConversionService {
    ConversionResponseDTO convertCurrency(ConversionRequestDTO dto);
}
