package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.infrastructure.filter.AutorizacionAccesoFilter;
import com.arquisoft.seguridad.infrastructure.filter.IdentidadTrazaFilter;
import com.arquisoft.seguridad.infrastructure.filter.JwtListaNegraFilter;
import com.arquisoft.seguridad.infrastructure.filter.ProveedorAutenticacionFilter;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties({PepProperties.class, EquivalenciasProperties.class, SesionProperties.class})
@RequiredArgsConstructor
public class SeguridadConfig {

    private final AppLogger logger;
    private final AccesoDenegadoHandler accesoDenegadoHandler;
    private final NoAutenticadoEntryPoint noAutenticadoEntryPoint;
    private final JwtListaNegraFilter jwtBlacklistFilter;
    private final GestorTraza gestorTraza;
    private final RutasPublicas rutasPublicas;
    private final AutenticacionProperties propiedadesAutenticacion;

    @Bean
    public IdentidadTrazaFilter seguridadIdentidadTrazaFilter() {
        return new IdentidadTrazaFilter(gestorTraza);
    }

    @Bean
    public FilterRegistrationBean<IdentidadTrazaFilter> seguridadIdentidadTrazaFilterSinRegistroServlet(
            IdentidadTrazaFilter filtro) {
        var registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    @Bean
    @ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
    public AutorizacionAccesoFilter seguridadAutorizacionAccesoFilter(AplicadorDecisionAcceso aplicadorDecisionAcceso) {
        return new AutorizacionAccesoFilter(aplicadorDecisionAcceso, rutasPublicas, propiedadesAutenticacion,
                noAutenticadoEntryPoint);
    }

    @Bean
    @ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
    public ProveedorAutenticacionFilter seguridadProveedorAutenticacionFilter(
            ProveedoresAutenticacion proveedores,
            ProteccionCsrf proteccionCsrf,
            AplicadorDecisionAcceso aplicadorDecisionAcceso,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedorExcepciones) {
        return new ProveedorAutenticacionFilter(proveedores, proteccionCsrf, aplicadorDecisionAcceso, rutasPublicas,
                noAutenticadoEntryPoint, accesoDenegadoHandler, resolvedorExcepciones, logger);
    }

    @Bean
    @ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<ProveedorAutenticacionFilter> seguridadProveedorAutenticacionFilterSinRegistroServlet(
            ProveedorAutenticacionFilter filtro) {
        var registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    @Bean
    @ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<AutorizacionAccesoFilter> seguridadAutorizacionAccesoFilterSinRegistroServlet(
            AutorizacionAccesoFilter filtro) {
        var registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    @Bean
    public SecurityFilterChain seguridadFilterChain(
            HttpSecurity http,
            AuthenticationManagerResolver<HttpServletRequest> seguridadAutenticacionPorEmisorResolver,
            ObjectProvider<ProveedorAutenticacionFilter> proveedorAutenticacionFilter,
            ObjectProvider<AutorizacionAccesoFilter> autorizacionAccesoFilter) throws Exception {
        http
                .cors(cors -> { })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(rutasPublicas.patronesPublicos()).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(noAutenticadoEntryPoint)
                        .authenticationManagerResolver(seguridadAutenticacionPorEmisorResolver)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(noAutenticadoEntryPoint)
                        .accessDeniedHandler(accesoDenegadoHandler)
                );

        // Canales de autenticacion distintos de Bearer (cookie de sesion del BFF): corre justo despues de
        // BearerTokenAuthenticationFilter y antes de la blacklist y de la traza, que leen el contexto que deja.
        proveedorAutenticacionFilter.ifAvailable(filtro -> http.addFilterAfter(filtro, BearerTokenAuthenticationFilter.class));

        // Verifica blacklist de tokens revocados en cada request autenticado.
        // Corre despues de BearerTokenAuthenticationFilter (autenticacion ya verificada).
        http.addFilterAfter(jwtBlacklistFilter, BearerTokenAuthenticationFilter.class);

        http.addFilterAfter(seguridadIdentidadTrazaFilter(), BearerTokenAuthenticationFilter.class);

        // Tras seguridadIdentidadTrazaFilter: cuando el proveedor responde 401/403/503 la traza ya lleva el sub.
        autorizacionAccesoFilter.ifAvailable(filtro -> http.addFilterAfter(filtro, BearerTokenAuthenticationFilter.class));

        return http.build();
    }

    // Ver la nota de CorsConfig: en tiempo de construccion del @Bean el catalogo de mensajes
    // aun puede no estar instalado y el log saldria como clave cruda, sin sus argumentos.
    @EventListener(ApplicationReadyEvent.class)
    public void registrarConfiguracionAplicada() {
        var emisores = propiedadesAutenticacion.habilitados().stream()
                .map(AutenticacionProperties.Emisor::id)
                .collect(Collectors.joining(","));
        var modos = propiedadesAutenticacion.habilitados().stream()
                .map(emisor -> emisor.authorization().name())
                .collect(Collectors.joining(","));
        logger.info(IniciarSesionKey.LOG_EMISORES_HABILITADOS, emisores, modos);
    }
}
