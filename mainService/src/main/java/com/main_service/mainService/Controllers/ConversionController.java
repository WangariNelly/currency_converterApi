package com.main_service.mainService.Controllers;

import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;
import com.main_service.mainService.Interfaces.ConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ConversionController {

    @Autowired
    private ConversionService conversionService;

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponseDTO> convert(@Valid @RequestBody ConversionRequestDTO conversionRequestDto) {
        ConversionResponseDTO response = conversionService.convertCurrency(conversionRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return ResponseEntity.ok().body("{\"status\": \"UP\"}");
    }
}
