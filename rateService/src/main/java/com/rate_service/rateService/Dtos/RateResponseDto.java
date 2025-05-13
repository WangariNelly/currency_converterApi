//package com.rate_service.rateService.Dtos;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.Map;
//
//@Data
//@AllArgsConstructor
//public class RateResponseDto {
//
//    @JsonProperty("base_code")
//    private String from;
//
//    @JsonProperty("target_code")
//    private String to;
//
//    @JsonProperty("conversion_rate")
//    private Double rate;
//
//    @JsonProperty("error")
//    private String error;
//
//    private String lastUpdated;
//
//    @JsonProperty("rates")
//    private Map<String, Double> rates;
//
//            public RateResponseDto(String error, String message, int i) {
//    }
//
//    public RateResponseDto() {
//    }
//
//    public RateResponseDto(String upperCase, String upperCase1, String s) {
//    }
//
//    // Helper method to get specific rate
//    public Double getRateForCurrency(String currency) {
//        return rates.get(currency.toUpperCase());
//    }
//
//    public String getFrom() {
//        return from;
//    }
//
//    public void setFrom(String from) {
//        this.from = from;
//    }
//
//    public String getTo() {
//        return to;
//    }
//
//    public void setTo(String to) {
//        this.to = to;
//    }
//
//    public Double getRate() {
//        return rate;
//    }
//
//    public void setRate(Double rate) {
//        this.rate = rate;
//    }
//
//    public Map<String, Double> getRates() {
//        return rates;
//    }
//
//    public void setRates(Map<String, Double> rates) {
//        this.rates = rates;
//    }
//    public String getError() {
//        return error;
//    }
//    public void setError(String error) {
//        this.error = error;
//    }
//
//    public String getLastUpdated() {
//        return lastUpdated;
//    }
//
//    public void setLastUpdated(String lastUpdated) {
//        this.lastUpdated = lastUpdated;
//    }
//}
