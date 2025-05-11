package com.main_service.mainService.Services;

import com.main_service.mainService.Dtos.ConversionRequestDTO;
import com.main_service.mainService.Dtos.ConversionResponseDTO;
import com.main_service.mainService.Dtos.RateResponseDTO;
import com.main_service.mainService.Interfaces.ConversionService;
import com.main_service.mainService.Models.Conversion;
import com.main_service.mainService.Repos.ConversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConversionServiceImpl implements ConversionService {
    @Autowired
    private ConversionRepository conversionRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${rate.service.api.key}")
    private String rateServiceApiKey;

    @Value("${rate.service.api.secret}")
    private String rateServiceApiSecret;

    @Value("${rate-service.url}")
    private String rateServiceUrl;

    @Override
    public ConversionResponseDTO convertCurrency(ConversionRequestDTO dto) {
//        String url = "/?from=" + dto.getFrom() + "&to=" + dto.getTo();

        try {
            var rateResponse = webClientBuilder.build().get()
//                    .uri("http://rate-service:8081/api/v1/rate/exchange" + url)
//                    .uri("http://localhost:8081" + url)
//                    .uri("http://rate-service:8081/api/v1/rate/exchange?from={from}&to={to}", from, to)
                    .uri(rateServiceUrl + "/exchange?from={from}&to={to}", dto.getFrom(), dto.getTo())
                    .header("X-API-KEY", rateServiceApiKey)
                    .header("X-API-SECRET", "rateServiceApiSecret")
                    .retrieve()
                    .onStatus(code -> !code.is2xxSuccessful(), response ->
                            Mono.error(new RuntimeException("Rate service failed: " + response.statusCode()))
                    )
                    .bodyToMono(RateResponseDTO.class)
                    .block();

            BigDecimal rate = BigDecimal.valueOf(rateResponse.getRate());
            BigDecimal result = dto.getAmount().multiply(rate);

            Conversion c = new Conversion();
            c.setFromCurrency(dto.getFrom());
            c.setToCurrency(dto.getTo());
            c.setAmount(dto.getAmount());
            c.setRate(rate);
            c.setResult(result);
            c.setTimestamp(LocalDateTime.now());

            conversionRepository.save(c);

            return new ConversionResponseDTO();

        } catch (Exception e) {
            throw new RuntimeException("Conversion failed: " + e.getMessage());
        }
    }
}
