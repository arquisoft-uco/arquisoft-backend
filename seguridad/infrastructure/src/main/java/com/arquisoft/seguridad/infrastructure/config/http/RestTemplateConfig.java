package com.arquisoft.seguridad.infrastructure.config.http;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Configuración de RestTemplate con timeouts, logging y retry.
 * Nota: RestTemplateBuilder fue eliminado en Spring Boot 4.0.x (ADR-008).
 * Se construye RestTemplate directamente con SimpleClientHttpRequestFactory.
 */
@Slf4j
@Configuration
public class RestTemplateConfig {

    @Value("${http.client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${http.client.read-timeout:30000}")
    private int readTimeout;

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        log.info("Configuring RestTemplate with connect timeout: {}ms, read timeout: {}ms",
                connectTimeout, readTimeout);

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(loggingInterceptor()));
        return restTemplate;
    }

    @Bean(name = "fastRestTemplate")
    public RestTemplate fastRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(5000);

        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(loggingInterceptor()));
        return restTemplate;
    }

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

    @Bean
    public SimpleClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
