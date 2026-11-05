package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionAutorizada;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class AutorizacionAccesoFilter extends OncePerRequestFilter {

    public static final String CABECERA_ID_DECISION = AplicadorDecisionAcceso.CABECERA_ID_DECISION;

    private final AplicadorDecisionAcceso aplicadorDecisionAcceso;
    private final RutasPublicas rutasPublicas;
    private final AutenticacionProperties propiedadesAutenticacion;
    private final NoAutenticadoEntryPoint puntoEntradaNoAutenticado;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var uri = request.getRequestURI();
        return CorsUtils.isPreFlightRequest(request) || rutasPublicas.esPublica(uri) || rutasPublicas.esExentaDePep(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var autenticacionActual = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacionActual instanceof AutenticacionAutorizada && autenticacionActual.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
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

        if (aplicadorDecisionAcceso.aplicar(request, response, emisor.get().id(), jwt.getTokenValue())) {
            filterChain.doFilter(request, response);
        }
    }

    private static InsufficientAuthenticationException sinAutenticacion() {
        return new InsufficientAuthenticationException(Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401));
    }
}
