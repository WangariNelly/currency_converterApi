package com.main_service.mainService.Dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ConversionRequestDTO {
    @NotBlank(message = "From currency is required")
    private String from;
    @NotBlank(message = "To currency is required")
    private String to;
    @NotNull @DecimalMin("0.0")
    private BigDecimal amount;

    public ConversionRequestDTO() {
    }

    public ConversionRequestDTO(String from, String to, BigDecimal amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
