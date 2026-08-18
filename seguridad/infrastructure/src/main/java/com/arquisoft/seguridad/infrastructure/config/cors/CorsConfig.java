package com.arquisoft.seguridad.infrastructure.config.cors;

import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.ConfiguracionKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.arquisoft.shared.tracing.infrastructure.traza.propagacion.TrazaHeaders;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig {
    
    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;
    
    @Value("${security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;
    
    // La SPA autentica con Bearer token (header Authorization), NO con cookies:
    // solo se necesitan estos headers en las requests entrantes. Evitamos "*".
    @Value("${security.cors.allowed-headers:Authorization,Content-Type}")
    private String allowedHeaders;

    @Value("${security.cors.max-age:3600}")
    private long maxAge;

    // Bearer (no cookies) ⇒ credentials=false. Evita el modo credentialed de CORS
    // y su restricción de no combinar con orígenes comodín.
    @Value("${security.cors.allow-credentials:false}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);
        
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size",
                TrazaHeaders.X_CORRELATION_ID,
                TrazaHeaders.X_TRANSACTION_ID
        ));
        
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);
        
        log.info(Mensajes.obtener(ConfiguracionKey.LOG_CORS_CONFIGURADO),
                origins, methods, maxAge);
        
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
