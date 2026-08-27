package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.seguridad.infrastructure.filter.IdentidadTrazaFilter;
import com.arquisoft.seguridad.infrastructure.filter.JwtBlacklistFilter;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SeguridadConfig {

    private final AppLogger logger;

    // Rutas públicas y plantilla del issuer: son contrato con Keycloak y con las herramientas
    // (actuator, springdoc), que las resuelven literalmente. No van al catálogo.
    private static final String PLANTILLA_ISSUER = "%s/realms/%s";
    private static final String RUTA_ACTUATOR_HEALTH = "/actuator/health/**";
    // Alloy raspa este endpoint por la red interna. Traefik no enruta /api/actuator
    // (ver labels en .github/workflows/deploy.yml), asi que no queda expuesto a internet.
    private static final String RUTA_ACTUATOR_PROMETHEUS = "/actuator/prometheus";
    private static final String RUTA_SWAGGER_UI = "/swagger-ui/**";
    private static final String RUTA_API_DOCS = "/v3/api-docs/**";
    private static final String RUTA_SWAGGER_RESOURCES = "/swagger-resources/**";

    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final SecurityAccessDeniedHandler securityAccessDeniedHandler;
    private final SecurityAuthenticationEntryPoint securityAuthenticationEntryPoint;
    private final JwtBlacklistFilter jwtBlacklistFilter;
    private final GestorTraza gestorTraza;
    private final RutasAutenticacion rutasAutenticacion;

    @Bean
    public IdentidadTrazaFilter identidadTrazaFilter() {
        return new IdentidadTrazaFilter(gestorTraza);
    }

    @Bean
    public FilterRegistrationBean<IdentidadTrazaFilter> identidadTrazaFilterSinRegistroServlet(
            IdentidadTrazaFilter filtro) {
        var registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    @Value("${arquisoft.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${arquisoft.keycloak.realm}")
    private String realm;

    @Value("${arquisoft.keycloak.expected-audience:arquisoft-api}")
    private String expectedAudience;

    @Bean
    public JwtDecoder jwtDecoder() {
        var issuer = PLANTILLA_ISSUER.formatted(keycloakServerUrl, realm);
        var decoder = NimbusJwtDecoder.withIssuerLocation(issuer).build();
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
                        .requestMatchers(HttpMethod.POST, rutasAutenticacion.login()).permitAll()
                        .requestMatchers(HttpMethod.POST, rutasAutenticacion.refresh()).permitAll()
                        .requestMatchers(HttpMethod.POST, rutasAutenticacion.validate()).permitAll()
                        .requestMatchers(RUTA_ACTUATOR_HEALTH, RUTA_ACTUATOR_PROMETHEUS).permitAll()
                        .requestMatchers(RUTA_SWAGGER_UI, RUTA_API_DOCS, RUTA_SWAGGER_RESOURCES).permitAll()
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

        http.addFilterAfter(identidadTrazaFilter(), BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    // Ver la nota de CorsConfig: en tiempo de construccion del @Bean el catalogo de mensajes
    // aun puede no estar instalado y el log saldria como clave cruda, sin sus argumentos.
    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        logger.info(Mensajes.obtener(IniciarSesionKey.LOG_JWT_DECODER_CONFIG),
                PLANTILLA_ISSUER.formatted(keycloakServerUrl, realm), expectedAudience);
    }
}
