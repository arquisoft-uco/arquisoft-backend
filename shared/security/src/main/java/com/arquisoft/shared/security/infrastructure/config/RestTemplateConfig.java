package com.arquisoft.shared.security.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuración de RestTemplate con timeouts, logging y retry.
 * Proporciona un RestTemplate robusto para comunicación con servicios externos.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${http.client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${http.client.read-timeout:30000}")
    private int readTimeout;

    /**
     * Bean principal de RestTemplate con configuración de timeouts y logging.
     */
    @Bean
    @Primary
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Configuring RestTemplate with connect timeout: {}ms, read timeout: {}ms", 
                connectTimeout, readTimeout);
        
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .additionalInterceptors(loggingInterceptor())
                .build();
    }

    /**
     * RestTemplate alternativo con timeouts más cortos para operaciones rápidas.
     */
    @Bean(name = "fastRestTemplate")
    public RestTemplate fastRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(2000))
                .setReadTimeout(Duration.ofMillis(5000))
                .additionalInterceptors(loggingInterceptor())
                .build();
    }

    /**
     * Interceptor para logging de requests y responses.
     */
    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug("HTTP Request: {} {}", request.getMethod(), request.getURI());
            
            long startTime = System.currentTimeMillis();
            var response = execution.execute(request, body);
            long duration = System.currentTimeMillis() - startTime;
            
            log.debug("HTTP Response: {} {} - Status: {} - Duration: {}ms", 
                    request.getMethod(), 
                    request.getURI(), 
                    response.getStatusCode(),
                    duration);
            
            return response;
        };
    }

    /**
     * Factory personalizada con configuración adicional.
     */
    @Bean
    public SimpleClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
