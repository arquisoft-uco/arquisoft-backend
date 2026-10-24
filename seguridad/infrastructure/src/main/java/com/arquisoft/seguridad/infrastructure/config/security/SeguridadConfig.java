package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.ResolvedorPlantillaRuta;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.filter.AutorizacionAccesoFilter;
import com.arquisoft.seguridad.infrastructure.filter.IdentidadTrazaFilter;
import com.arquisoft.seguridad.infrastructure.filter.JwtListaNegraFilter;
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
@EnableConfigurationProperties({PepProperties.class, EquivalenciasProperties.class})
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
    public AutorizacionAccesoFilter seguridadAutorizacionAccesoFilter(
            AutorizarAccesoInteractor autorizarAccesoInteractor,
            ResolvedorPlantillaRuta resolvedorPlantillaRuta,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedorExcepciones) {
        return new AutorizacionAccesoFilter(autorizarAccesoInteractor, resolvedorPlantillaRuta, gestorTraza,
                rutasPublicas, propiedadesAutenticacion, noAutenticadoEntryPoint, accesoDenegadoHandler, resolvedorExcepciones);
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
