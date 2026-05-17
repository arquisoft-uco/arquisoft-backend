package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.filter.JwtBlacklistFilter;
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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para OAuth2/JWT con Keycloak.
 * Configura:
 * - Validación de JWT basada en las claves públicas de Keycloak (JWK Set URI)
 * - Conversión de roles Keycloak a GrantedAuthority via KeycloakJwtConverterConfig
 * - Políticas de sesión stateless
 * - Protección CSRF deshabilitada (API REST)
 * - Rutas públicas y protegidas
 */
@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;
    private final SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;
    private final JwtBlacklistFilter jwtBlacklistFilter;

    @Value("${arquisoft.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${arquisoft.keycloak.realm}")
    private String realm;

    @Bean
    public JwtDecoder jwtDecoder() {
        String jwkSetUri = String.format(
                "%s/realms/%s/protocol/openid-connect/certs",
                keycloakServerUrl, realm
        );
        log.info("Configuring JWT decoder with JWK Set URI: {}", jwkSetUri);
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> { })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/validate").permitAll()
                        .requestMatchers("/actuator/health/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(securityAuthenticationEntryPoint)
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityAuthenticationEntryPoint)
                        .accessDeniedHandler(securityAccessDeniedHandler)
                );

        // Verifica blacklist de tokens revocados en cada request autenticado.
        // Corre despues de BearerTokenAuthenticationFilter (autenticacion ya verificada).
        http.addFilterAfter(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }
}
