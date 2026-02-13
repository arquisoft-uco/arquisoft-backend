package com.arquisoft.shared.security.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración de Spring Security para OAuth2/JWT con Keycloak.
 * Configura:
 * - Validación de JWT basada en las claves públicas de Keycloak
 * - Políticas de sesión stateless
 * - Protección CSRF
 * - Rutas públicas y protegidas
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    
    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${security.public-endpoints:}")
    private String publicEndpoints;

    /**
     * Decodificador JWT que obtiene las claves públicas de Keycloak.
     * Las claves se obtienen del endpoint de JWKS de Keycloak.
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = String.format(
                "%s/realms/%s/protocol/openid-connect/certs",
                keycloakServerUrl, realm
        );
        log.info("Configuring JWT decoder with JWK Set URI: {}", jwkSetUri);
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * RestTemplate para comunicación con Keycloak.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * Configuración de la cadena de filtros de seguridad.
     * Define:
     * - Todas las solicitudes requieren autenticación (excepto rutas públicas)
     * - Uso de OAuth2 con JWT
     * - Política de sesión stateless
     * - CSRF deshabilitado (para APIs REST)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS configurado en CorsConfig
                .cors(cors -> {})
                
                // CSRF deshabilitado para APIs REST
                .csrf(csrf -> csrf.disable())
                
                // Política de sesión stateless (no usar cookies de sesión)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Configuración de autorización
                .authorizeHttpRequests(authz -> authz
                        // Rutas públicas
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/validate").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        
                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )
                
                // Configuración OAuth2 Resource Server
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }
}
