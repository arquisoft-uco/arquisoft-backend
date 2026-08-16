package com.arquisoft.seguridad.infrastructure.config.http;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.ConfiguracionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
        log.info(Mensajes.obtener(ConfiguracionKey.LOG_REST_TEMPLATE_CONFIGURADO),
                connectTimeout, readTimeout);

        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        var restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(loggingInterceptor()));
        return restTemplate;
    }

    @Bean(name = "fastRestTemplate")
    public RestTemplate fastRestTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000);
        factory.setReadTimeout(5000);

        var restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(loggingInterceptor()));
        return restTemplate;
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.debug(Mensajes.obtener(ConfiguracionKey.LOG_HTTP_PETICION),
                    request.getMethod(), request.getURI());

            long startTime = System.currentTimeMillis();
            var response = execution.execute(request, body);
            long duration = System.currentTimeMillis() - startTime;

            log.debug(Mensajes.obtener(ConfiguracionKey.LOG_HTTP_RESPUESTA),
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    duration);

            return response;
        };
    }

    @Bean
    public SimpleClientHttpRequestFactory clientHttpRequestFactory() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
