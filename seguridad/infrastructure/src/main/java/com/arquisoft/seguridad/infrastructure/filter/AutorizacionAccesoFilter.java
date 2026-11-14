package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.AplicadorListaNegra;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.IdentidadAutenticada;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.ProteccionCsrf;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import com.arquisoft.shared.web.filter.RutasTecnicas;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class AutorizacionAccesoFilter extends OncePerRequestFilter {

    public static final String CABECERA_ID_DECISION = AplicadorDecisionAcceso.CABECERA_ID_DECISION;

    private static final String CABECERA_ORIGIN = "Origin";
    private static final String CABECERA_REFERER = "Referer";

    private final ProveedoresAutenticacion proveedores;
    private final ProteccionCsrf proteccionCsrf;
    private final AplicadorDecisionAcceso aplicadorDecisionAcceso;
    private final AplicadorListaNegra aplicadorListaNegra;
    private final GestorTraza gestorTraza;
    private final RutasPublicas rutasPublicas;
    private final NoAutenticadoEntryPoint puntoEntradaNoAutenticado;
    private final AccesoDenegadoHandler manejadorAccesoDenegado;
    private final HandlerExceptionResolver resolvedorExcepciones;
    private final AppLogger appLogger;
    private final boolean decisionHabilitada;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return CorsUtils.isPreFlightRequest(request) || rutasPublicas.esPublica(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var uri = request.getRequestURI();
        var rutaExenta = rutasPublicas.esExentaDePep(uri);
        var rutaNoDeNegocio = rutaExenta || RutasTecnicas.esRutaTecnica(uri);

        SeleccionProveedor.Seleccion seleccion;
        try {
            seleccion = SeleccionProveedor.resolver(request, proveedores, rutaNoDeNegocio);
        } catch (AuthenticationException e) {
            puntoEntradaNoAutenticado.commence(request, response, e);
            return;
        } catch (RuntimeException e) {
            resolvedorExcepciones.resolveException(request, response, null, e);
            return;
        }

        switch (seleccion) {
            case SeleccionProveedor.Seleccion.NoActua ignorada ->
                    puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
            case SeleccionProveedor.Seleccion.Rechazada rechazada -> {
                appLogger.warn(ResolucionSesionKey.LOG_SELECCION_RECHAZADA, rechazada.motivo());
                puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
            }
            case SeleccionProveedor.Seleccion.Elegida elegida -> {
                if (procesar(request, response, elegida.proveedor(), elegida.credencial(), elegida.emisor(), rutaExenta)) {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }

    private boolean procesar(HttpServletRequest request, HttpServletResponse response, ProveedorAutenticacion proveedor,
                             String credencial, String emisor, boolean rutaExenta) {
        if (proveedor.exigeProteccionCsrf() && !csrfPermitido(request, response)) {
            return false;
        }
        var decidir = decisionHabilitada && !rutaExenta;

        if (proveedor.autenticaPrimero()) {
            var identidad = autenticar(request, response, proveedor, credencial, emisor);
            if (identidad.isEmpty() || !aplicadorListaNegra.aplicar(request, response, identidad.get())) {
                return false;
            }
            gestorTraza.registrarUsuario(identidad.get().sujeto());
            return !decidir
                    || aplicadorDecisionAcceso.aplicar(request, response, identidad.get().emisor(), credencial);
        }

        if (decidir && !aplicadorDecisionAcceso.aplicar(request, response, proveedor.id(), credencial)) {
            return false;
        }
        var identidad = autenticar(request, response, proveedor, credencial, emisor);
        if (identidad.isEmpty() || !aplicadorListaNegra.aplicar(request, response, identidad.get())) {
            return false;
        }
        gestorTraza.registrarUsuario(identidad.get().sujeto());
        return true;
    }

    private boolean csrfPermitido(HttpServletRequest request, HttpServletResponse response) {
        var resultado = proteccionCsrf.evaluar(request.getMethod(), request.getHeader(ProteccionCsrf.CABECERA_XSRF),
                request.getHeader(CABECERA_ORIGIN), request.getHeader(CABECERA_REFERER));
        if (resultado == ProteccionCsrf.Resultado.PERMITIDA) {
            return true;
        }
        appLogger.warn(ResolucionSesionKey.LOG_CSRF_RECHAZADO, request.getMethod(), resultado.name());
        manejadorAccesoDenegado.handle(request, response, new AccessDeniedException(Mensajes.obtener(HttpKey.PROHIBIDO)));
        return false;
    }

    private Optional<IdentidadAutenticada> autenticar(HttpServletRequest request, HttpServletResponse response,
                                                      ProveedorAutenticacion proveedor, String credencial,
                                                      String emisor) {
        try {
            var identidad = proveedor.autenticar(credencial, emisor);
            var contexto = SecurityContextHolder.createEmptyContext();
            contexto.setAuthentication(identidad);
            SecurityContextHolder.setContext(contexto);
            return Optional.of(identidad);
        } catch (AuthenticationServiceException e) {
            resolvedorExcepciones.resolveException(request, response, null, e);
        } catch (AuthenticationException e) {
            puntoEntradaNoAutenticado.commence(request, response, e);
        } catch (RuntimeException e) {
            resolvedorExcepciones.resolveException(request, response, null, e);
        }
        return Optional.empty();
    }

    private static InsufficientAuthenticationException sinAutenticacion() {
        return new InsufficientAuthenticationException(Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401));
    }
}
