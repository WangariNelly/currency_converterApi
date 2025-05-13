package com.main_service.mainService.Controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;
import com.main_service.mainService.Interfaces.ConversionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ConversionController {

    private final ConversionService conversionService;
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }


//    @PostMapping("/convert")
//    public ResponseEntity<JsonNode> convert(@RequestBody Map<String, Object> request) {
//        try {
//            String from = (String) request.get("from");
//            String to = (String) request.get("to");
//            double amount = Double.parseDouble(request.get("amount").toString());
//
//            JsonNode response = conversionService.convertCurrency(from, to, amount);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    (JsonNode) Map.of("error", "Invalid request format or missing fields")
//            );
//        }
//    }

    @PostMapping("/convert")
    public ResponseEntity<JsonNode> convert(@RequestBody JsonNode requestBody) {
        String from = requestBody.path("from").asText();
        String to = requestBody.path("to").asText();
        double amount = requestBody.path("amount").asDouble();

        if (from.isEmpty() || to.isEmpty() || amount <= 0) {
            return ResponseEntity.badRequest().body(createErrorResponse("Invalid input parameters"));
        }

        JsonNode response = conversionService.convertCurrency(from, to, amount);
        return ResponseEntity.ok(response);
    }

    private JsonNode createErrorResponse(String message) {
        return JsonNodeFactory.instance.objectNode().put("error", message);
    }


    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> status() {
        return ResponseEntity.ok(Map.of("status", "UP"));
    }
}
