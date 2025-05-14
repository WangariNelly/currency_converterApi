package com.main_service.mainService.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;
import com.main_service.mainService.Interfaces.ConversionService;
import com.main_service.mainService.Services.ConversionServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ConversionController {



    private final ConversionServiceImpl conversionServiceIm;
    public ConversionController(ConversionServiceImpl conversionServiceIm) {
        this.conversionServiceIm = conversionServiceIm;
    }
//    @PostMapping("/convert")
//    public Mono<JsonNode> convert(@RequestBody JsonNode requestBody) {
//        String from = requestBody.path("from").asText();
//        String to = requestBody.path("to").asText();
//        double amount = requestBody.path("amount").asDouble();
//
//        if (isValidCurrencyCode(from) && isValidCurrencyCode(to)) {
//            return conversionServiceIm.convertCurrency(from.toUpperCase(), to.toUpperCase(),amount);
//        }
//
//
//        ObjectNode error = JsonNodeFactory.instance.objectNode();
//        error.put("error", "Invalid currency code(s)");
//        error.put("from", from);
//        error.put("to", to);
//        error.put("rate", 0.0);
//        error.put("amount", amount);
//        error.put("converted_amount", 0.0);
//        error.put("last_updated", "");
//        return Mono.just(error);
//
//    }
//
//    private JsonNode createErrorResponse(String message) {
//        return JsonNodeFactory.instance.objectNode().put("error", message);
//    }

    @PostMapping("/convert")
    public Mono<JsonNode> convert(@RequestBody JsonNode requestBody) {
        if (requestBody == null || !requestBody.has("from") || !requestBody.has("to") || !requestBody.has("amount")) {
            return Mono.just(createErrorResponse("Invalid request body"));
        }
        String from = requestBody.path("from").asText();
        String to = requestBody.path("to").asText();
        double amount = requestBody.path("amount").asDouble();

        if (!isValidCurrencyCode(from) || !isValidCurrencyCode(to)) {
            ObjectNode error = JsonNodeFactory.instance.objectNode();
            error.put("error", "Invalid currency code(s)");
            error.put("from", from);
            error.put("to", to);
            error.put("rate", 0.0);
            error.put("amount", amount);
            error.put("converted_amount", 0.0);
            error.put("last_updated", "");
            return Mono.just(error);
        }

        return conversionServiceIm.convertCurrency(from.toUpperCase(), to.toUpperCase(), amount)
                .defaultIfEmpty(createErrorResponse("Conversion failed or no data returned"));
    }

    private JsonNode createErrorResponse(String message) {
        return JsonNodeFactory.instance.objectNode().put("error", message);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }

    private boolean isValidCurrencyCode(String code) {
        return code != null && code.matches("^[A-Z]{3}$");
    }
}
