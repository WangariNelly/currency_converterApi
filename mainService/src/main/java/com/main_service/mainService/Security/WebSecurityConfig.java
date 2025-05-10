package com.main_service.mainService.Security;

import com.main_service.mainService.Filters.ApiKeyFilter;
import com.main_service.mainService.Filters.RateLimitFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity

public class WebSecurityConfig {

    private final ApiKeyFilter apiKeyFilter;
    private final RateLimitFilter rateLimitFilter;

    public WebSecurityConfig(ApiKeyFilter apiKeyFilter, RateLimitFilter rateLimitFilter) {
        this.apiKeyFilter = apiKeyFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
      http
//              .addFilterBefore(rateLimitFilter,ApiKeyFilter.class)
              .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/status", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(apiKeyFilter, BasicAuthenticationFilter.class);
        return http.build();
    }
}
