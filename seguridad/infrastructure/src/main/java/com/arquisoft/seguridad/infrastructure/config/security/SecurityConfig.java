package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.message.key.seguridad.LoginKey;
import com.arquisoft.shared.message.MessageCatalog;
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
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

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
    private final MessageCatalog catalog;

    @Value("${arquisoft.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${arquisoft.keycloak.realm}")
    private String realm;

    @Value("${arquisoft.keycloak.expected-audience:arquisoft-api}")
    private String expectedAudience;

    @Value("${rutas.seguridad.auth.base:/auth}${rutas.seguridad.auth.login:/login}")
    private String authLoginPath;

    @Value("${rutas.seguridad.auth.base:/auth}${rutas.seguridad.auth.refresh:/refresh}")
    private String authRefreshPath;

    @Value("${rutas.seguridad.auth.base:/auth}${rutas.seguridad.auth.validate:/validate}")
    private String authValidatePath;

    @Bean
    public JwtDecoder jwtDecoder() {
        String issuer = String.format("%s/realms/%s", keycloakServerUrl, realm);
        log.info(catalog.obtener(LoginKey.LOG_JWT_DECODER_CONFIG), issuer, expectedAudience);

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
        decoder.setJwtValidator(jwtValidator(issuer, expectedAudience));
        return decoder;
    }

    static OAuth2TokenValidator<Jwt> jwtValidator(String issuer, String expectedAudience) {
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        return new DelegatingOAuth2TokenValidator<>(
                withIssuer, new AudienceValidator(expectedAudience));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> { })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(HttpMethod.POST, authLoginPath).permitAll()
                        .requestMatchers(HttpMethod.POST, authRefreshPath).permitAll()
                        .requestMatchers(HttpMethod.POST, authValidatePath).permitAll()
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
