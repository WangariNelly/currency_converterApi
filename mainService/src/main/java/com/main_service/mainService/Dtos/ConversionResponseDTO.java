package com.main_service.mainService.Dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResponseDTO {
    private BigDecimal result;
    private String from;
    private String to;
    private BigDecimal amount;
    private BigDecimal rate;
    private LocalDateTime timestamp;


}
