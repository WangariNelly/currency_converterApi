package com.main_service.mainService;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConversionServiceTest {
    // This is a placeholder for the actual test class.
    // You would typically use a testing framework like JUnit or TestNG to write your tests.
    // For example, you could write a test method to verify the conversion logic.

    @Test
    void shouldConvertAmountCorrectly() {
        ConversionService service = new ConversionService();

        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("153.75");

        BigDecimal result = service.convert(amount, rate);

        assertEquals(new BigDecimal("15375.00"), result);
    }
}
