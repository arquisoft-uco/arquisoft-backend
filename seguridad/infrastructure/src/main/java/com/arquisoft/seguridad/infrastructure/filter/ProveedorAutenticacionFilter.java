package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.ProteccionCsrf;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.app.HttpKey;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import com.arquisoft.shared.web.filter.RutasTecnicas;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@RequiredArgsConstructor
public class ProveedorAutenticacionFilter extends OncePerRequestFilter {

    private static final String CABECERA_ORIGIN = "Origin";
    private static final String CABECERA_REFERER = "Referer";

    private final ProveedoresAutenticacion proveedores;
    private final ProteccionCsrf proteccionCsrf;
    private final AplicadorDecisionAcceso aplicadorDecisionAcceso;
    private final RutasPublicas rutasPublicas;
    private final NoAutenticadoEntryPoint puntoEntradaNoAutenticado;
    private final AccesoDenegadoHandler manejadorAccesoDenegado;
    private final HandlerExceptionResolver resolvedorExcepciones;
    private final AppLogger appLogger;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        var uri = request.getRequestURI();
        return CorsUtils.isPreFlightRequest(request) || rutasPublicas.esPublica(uri)
                || RutasTecnicas.esRutaTecnica(uri) || rutasPublicas.esExentaDePep(uri);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var hayBearerAutenticado = SecurityContextHolder.getContext().getAuthentication()
                instanceof JwtAuthenticationToken bearer && bearer.isAuthenticated();

        switch (SeleccionProveedor.resolver(request, hayBearerAutenticado, proveedores)) {
            case SeleccionProveedor.Seleccion.NoActua ignorada -> filterChain.doFilter(request, response);
            case SeleccionProveedor.Seleccion.Rechazada rechazada -> {
                appLogger.warn(ResolucionSesionKey.LOG_SELECCION_RECHAZADA, rechazada.motivo());
                puntoEntradaNoAutenticado.commence(request, response, sinAutenticacion());
            }
            case SeleccionProveedor.Seleccion.Elegida elegida -> autenticarYContinuar(request, response, filterChain,
                    elegida);
        }
    }

    private void autenticarYContinuar(HttpServletRequest request, HttpServletResponse response,
                                      FilterChain filterChain, SeleccionProveedor.Seleccion.Elegida elegida)
            throws ServletException, IOException {
        var proveedor = elegida.proveedor();

        if (proveedor.exigeProteccionCsrf()) {
            var resultado = proteccionCsrf.evaluar(request.getMethod(), request.getHeader(ProteccionCsrf.CABECERA_XSRF),
                    request.getHeader(CABECERA_ORIGIN), request.getHeader(CABECERA_REFERER));
            if (resultado != ProteccionCsrf.Resultado.PERMITIDA) {
                appLogger.warn(ResolucionSesionKey.LOG_CSRF_RECHAZADO, request.getMethod(), resultado.name());
                manejadorAccesoDenegado.handle(request, response,
                        new AccessDeniedException(Mensajes.obtener(HttpKey.PROHIBIDO)));
                return;
            }
        }

        if (!aplicadorDecisionAcceso.aplicar(request, response, proveedor.id(), elegida.credencial())) {
            return;
        }

        try {
            var contexto = SecurityContextHolder.createEmptyContext();
            contexto.setAuthentication(proveedor.autenticar(elegida.credencial()));
            SecurityContextHolder.setContext(contexto);
        } catch (AuthenticationException e) {
            puntoEntradaNoAutenticado.commence(request, response, e);
            return;
        } catch (RuntimeException e) {
            resolvedorExcepciones.resolveException(request, response, null, e);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static InsufficientAuthenticationException sinAutenticacion() {
        return new InsufficientAuthenticationException(Mensajes.obtener(IniciarSesionKey.ERROR_HTTP_401));
    }
}
