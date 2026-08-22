package com.arquisoft.seguridad.infrastructure.config.http;

import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.web.client.TrazaClientHttpRequestInterceptor;
import com.arquisoft.shared.message.key.seguridad.ConfiguracionKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final AppLogger logger;
    private final TrazaClientHttpRequestInterceptor trazaInterceptor;

    @Value("${http.client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${http.client.read-timeout:30000}")
    private int readTimeout;

    @Bean
    @Primary
    public RestTemplate restTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        var restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(trazaInterceptor, loggingInterceptor()));
        return restTemplate;
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            logger.debug(Mensajes.obtener(ConfiguracionKey.LOG_HTTP_PETICION),
                    request.getMethod(), request.getURI());

            long startTime = System.currentTimeMillis();
            var response = execution.execute(request, body);
            long duration = System.currentTimeMillis() - startTime;

            logger.debug(Mensajes.obtener(ConfiguracionKey.LOG_HTTP_RESPUESTA),
                    request.getMethod(),
                    request.getURI(),
                    response.getStatusCode(),
                    duration);

            return response;
        };
    }

    // Ver la nota de CorsConfig: en tiempo de construccion del @Bean el catalogo de mensajes
    // aun puede no estar instalado y el log saldria como clave cruda, sin sus argumentos.
    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        logger.info(Mensajes.obtener(ConfiguracionKey.LOG_REST_TEMPLATE_CONFIGURADO),
                connectTimeout, readTimeout);
    }
}
