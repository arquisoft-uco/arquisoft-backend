package com.arquisoft.seguridad.infrastructure.config.http;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.shared.web.client.TrazaClientHttpRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SesionRestTemplateConfig {

    public static final String BEAN = "seguridadSesionRestTemplate";

    private final SesionProperties propiedades;
    private final TrazaClientHttpRequestInterceptor trazaInterceptor;

    @Bean(BEAN)
    public RestTemplate seguridadSesionRestTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(propiedades.connectTimeout());
        factory.setReadTimeout(propiedades.readTimeout());

        var restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(trazaInterceptor));
        return restTemplate;
    }
}
