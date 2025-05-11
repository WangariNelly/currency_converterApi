package com.rate_service.rateService.Configurations;

import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;


@Configuration
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
               return WebClient.builder()
//                .baseUrl("https://api.exchangerate-api.com/v4/latest/")
                .baseUrl("https://v6.exchangerate-api.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
//                       .clientConnector(new ReactorClientHttpConnector(
//                               HttpClient.create().secure(sslContextSpec ->
//                                       sslContextSpec.sslContext(
//                                               SslContextBuilder.forClient()
//                                                       .trustManager(InsecureTrustManagerFactory.INSTANCE)
//                                       )
//                               )
//                       ))
                .build();
    }
}
