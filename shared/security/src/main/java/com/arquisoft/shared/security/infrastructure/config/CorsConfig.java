package com.arquisoft.shared.security.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de CORS (Cross-Origin Resource Sharing).
 * Permite solicitudes desde orígenes específicos (localhost, dominios, IPs).
 * 
 * Las propiedades se configuran en application.properties:
 * - security.cors.allowed-origins
 * - security.cors.allowed-methods
 * - security.cors.allowed-headers
 * - security.cors.max-age
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class CorsConfig {
    
    @Value("${security.cors.allowed-origins:http://localhost:3000,http://localhost:4200}")
    private String allowedOrigins;
    
    @Value("${security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private String allowedMethods;
    
    @Value("${security.cors.allowed-headers:*}")
    private String allowedHeaders;
    
    @Value("${security.cors.max-age:3600}")
    private long maxAge;
    
    @Value("${security.cors.allow-credentials:true}")
    private boolean allowCredentials;

    /**
     * Fuente de configuración CORS.
     * Define qué orígenes pueden acceder a la API y qué métodos/headers están permitidos.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Parsar y configurar orígenes permitidos
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);
        
        // Configurar métodos HTTP permitidos
        List<String> methods = Arrays.asList(allowedMethods.split(","));
        configuration.setAllowedMethods(methods);
        
        // Configurar headers permitidos
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        
        // Configurar headers expuestos (que el cliente puede acceder)
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size"
        ));
        
        // Permitir credenciales (cookies, headers de autorización)
        configuration.setAllowCredentials(allowCredentials);
        
        // Tiempo máximo de caché de la configuración CORS
        configuration.setMaxAge(maxAge);
        
        log.info("CORS configuration: origins={}, methods={}, maxAge={}", 
                origins, methods, maxAge);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
