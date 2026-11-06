package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.AplicadorListaNegra;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.infrastructure.filter.AutorizacionAccesoFilter;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
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
    private final GestorTraza gestorTraza;
    private final RutasPublicas rutasPublicas;
    private final AutenticacionProperties propiedadesAutenticacion;

    @Bean
    public AutorizacionAccesoFilter seguridadAutorizacionAccesoFilter(
            ProveedoresAutenticacion proveedores,
            ProteccionCsrf proteccionCsrf,
            AplicadorDecisionAcceso aplicadorDecisionAcceso,
            AplicadorListaNegra aplicadorListaNegra,
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedorExcepciones,
            @Value("${security.enabled:true}") boolean decisionHabilitada) {
        return new AutorizacionAccesoFilter(proveedores, proteccionCsrf, aplicadorDecisionAcceso, aplicadorListaNegra,
                gestorTraza, rutasPublicas, noAutenticadoEntryPoint, accesoDenegadoHandler, resolvedorExcepciones,
                logger, decisionHabilitada);
    }

    @Bean
    public FilterRegistrationBean<AutorizacionAccesoFilter> seguridadAutorizacionAccesoFilterSinRegistroServlet(
            AutorizacionAccesoFilter filtro) {
        var registro = new FilterRegistrationBean<>(filtro);
        registro.setEnabled(false);
        return registro;
    }

    @Bean
    public SecurityFilterChain seguridadFilterChain(HttpSecurity http, AutorizacionAccesoFilter autorizacionAccesoFilter)
            throws Exception {
        http
                .cors(cors -> { })
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(rutasPublicas.patronesPublicos()).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(noAutenticadoEntryPoint)
                        .accessDeniedHandler(accesoDenegadoHandler)
                );

        // BearerTokenAuthenticationFilter ya no esta en la cadena (el Bearer es un ProveedorAutenticacion mas);
        // solo se usa como ancla de orden. El filtro unico autentica, aplica lista negra y traza, y decide el acceso.
        http.addFilterAfter(autorizacionAccesoFilter, BearerTokenAuthenticationFilter.class);

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
