package com.arquisoft.usuarios.infrastructure.config.http;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.web.client.TrazaClientHttpRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class UsuariosRestTemplateConfig {

    public static final String BEAN = "usuariosRestTemplate";

    private final AppLogger logger;
    private final TrazaClientHttpRequestInterceptor trazaInterceptor;

    @Value("${http.client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${http.client.read-timeout:30000}")
    private int readTimeout;

    @Bean(BEAN)
    public RestTemplate usuariosRestTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);

        var restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(trazaInterceptor));
        return restTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        logger.info(RegistrarUsuarioKey.LOG_REST_TEMPLATE_CONFIGURADO, connectTimeout, readTimeout);
    }
}
