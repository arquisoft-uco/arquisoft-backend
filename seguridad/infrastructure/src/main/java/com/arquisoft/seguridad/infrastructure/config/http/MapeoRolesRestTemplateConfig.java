package com.arquisoft.seguridad.infrastructure.config.http;

import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.KeycloakFuenteMapeoRolesProperties;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.MapeoRolesProperties;
import com.arquisoft.shared.web.client.TrazaClientHttpRequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({KeycloakFuenteMapeoRolesProperties.class, MapeoRolesProperties.class})
public class MapeoRolesRestTemplateConfig {

    public static final String BEAN = "seguridadMapeoRolesRestTemplate";

    private final KeycloakFuenteMapeoRolesProperties propiedades;
    private final TrazaClientHttpRequestInterceptor trazaInterceptor;

    @Bean(BEAN)
    public RestTemplate seguridadMapeoRolesRestTemplate() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(propiedades.connectTimeout());
        factory.setReadTimeout(propiedades.readTimeout());

        var restTemplate = new RestTemplate(factory);
        restTemplate.setInterceptors(List.of(trazaInterceptor));
        return restTemplate;
    }
}
