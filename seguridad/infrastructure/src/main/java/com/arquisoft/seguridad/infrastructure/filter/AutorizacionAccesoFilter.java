package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.ResolvedorPlantillaRuta;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.mapper.AutorizarAccesoRequestMapper;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
public class AutorizacionAccesoFilter extends OncePerRequestFilter {

    public static final String CABECERA_ID_DECISION = "X-Decision-Id";

    private final AutorizarAccesoInteractor autorizarAccesoInteractor;
    private final ResolvedorPlantillaRuta resolvedorPlantillaRuta;
    private final GestorTraza gestorTraza;
    private final RutasPublicas rutasPublicas;
    private final AutenticacionProperties propiedadesAutenticacion;
    private final NoAutenticadoEntryPoint puntoEntradaNoAutenticado;
    private final AccesoDenegadoHandler manejadorAccesoDenegado;
    private final HandlerExceptionResolver resolvedorExcepciones;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var uri = request.getRequestURI();
        return CorsUtils.isPreFlightRequest(request) || rutasPublicas.esPublica(uri) || rutasPublicas.esExentaDePep(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var autenticacionActual = SecurityContextHolder.getContext().getAuthentication();
        if (!(autenticacionActual instanceof JwtAuthenticationToken jwtAuth) || !jwtAuth.isAuthenticated()) {
            puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
            return;
        }

        var jwt = jwtAuth.getToken();
        var emisor = propiedadesAutenticacion.porIssuer(String.valueOf(jwt.getIssuer()));
        if (emisor.isEmpty()) {
            puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
            return;
        }

        DecisionAccesoResult decision;
        try {
            decision = autorizarAccesoInteractor.ejecutar(AutorizarAccesoRequestMapper.toCommand(
                    request, resolvedorPlantillaRuta.identificarRecurso(request), emisor.get().id(),
                    jwt.getTokenValue(), gestorTraza.correlacionActual()));
        } catch (RuntimeException e) {
            resolvedorExcepciones.resolveException(request, response, null, e);
            return;
        }

        switch (decision) {
            case DecisionAccesoResult.Permitida permitida -> {
                if (!permitida.idDecision().isBlank()) {
                    response.setHeader(CABECERA_ID_DECISION, permitida.idDecision());
                }
                filterChain.doFilter(request, response);
            }
            case DecisionAccesoResult.NoAutenticada ignorada ->
                    puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
            case DecisionAccesoResult.Denegada ignorada ->
                    manejadorAccesoDenegado.handle(request, response,
                            new AccessDeniedException(Mensajes.obtener(HttpKey.PROHIBIDO)));
        }
    }

    private static InsufficientAuthenticationException sinAutenticacion() {
        return new InsufficientAuthenticationException(Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401));
    }
}
