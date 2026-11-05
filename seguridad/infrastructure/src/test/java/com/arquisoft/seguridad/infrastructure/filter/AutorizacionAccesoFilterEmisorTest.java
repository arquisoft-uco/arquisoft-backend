package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.AplicadorListaNegra;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.BearerAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.ProteccionCsrf;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacionAccesoFilterEmisorTest {

    @Mock
    private AplicadorDecisionAcceso aplicadorDecision;

    @Mock
    private AplicadorListaNegra aplicadorListaNegra;

    @Mock
    private ProteccionCsrf proteccionCsrf;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private NoAutenticadoEntryPoint entryPoint;

    @Mock
    private AccesoDenegadoHandler accesoDenegadoHandler;

    @Mock
    private HandlerExceptionResolver resolvedorExcepciones;

    @Mock
    private AppLogger appLogger;

    @Mock
    private FilterChain cadena;

    private ProveedorAutenticacion bearer;
    private BearerAuthenticationToken identidadBearer;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void preparar() {
        bearer = mock(ProveedorAutenticacion.class);
        lenient().when(bearer.id()).thenReturn("bearer");
        lenient().when(bearer.habilitado()).thenReturn(true);
        lenient().when(bearer.autenticaPrimero()).thenReturn(true);
        lenient().when(bearer.actuaEnRutasNoDeNegocio()).thenReturn(true);
        identidadBearer = new BearerAuthenticationToken("principal", "sub-1", null, null, List.of(), "jti-1", null);
        lenient().when(aplicadorListaNegra.aplicar(any(), any(), any())).thenReturn(true);
        request = new MockHttpServletRequest("GET", "/api/fichas-perfil/123");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private AutorizacionAccesoFilter filtro() {
        var rutas = new RutasPublicas("/api", List.of("/actuator/health/**"), List.of("/auth/logout"), "none",
                new MockEnvironment());
        return new AutorizacionAccesoFilter(new ProveedoresAutenticacion(List.of(bearer)), proteccionCsrf,
                aplicadorDecision, aplicadorListaNegra, gestorTraza, rutas, entryPoint, accesoDenegadoHandler,
                resolvedorExcepciones, appLogger, true);
    }

    private void conBearerForzado(String emisor, BearerAuthenticationToken identidad) {
        lenient().when(bearer.emisorHabilitado(emisor)).thenReturn(Optional.of(emisor));
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-1"));
        when(bearer.autenticar("tok-1", emisor)).thenReturn(identidad);
        request.addHeader(SeleccionProveedor.CABECERA, emisor);
    }

    @Test
    void debeDecidirConElModoDelEmisorForzado_cuandoLlegaElEncabezadoLegado() throws Exception {
        // Arrange
        var identidadLegado = new BearerAuthenticationToken("legado", "sub-3", null, null, List.of(), "jti-3", null);
        conBearerForzado("legado", identidadLegado);
        when(aplicadorDecision.aplicar(request, response, "legado", "tok-1")).thenReturn(true);

        // Act
        filtro().doFilter(request, response, cadena);

        // Assert
        verify(aplicadorListaNegra).aplicar(request, response, identidadLegado);
        verify(aplicadorDecision).aplicar(request, response, "legado", "tok-1");
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeDecidirPorPep_cuandoElEncabezadoEsPrincipal() throws Exception {
        // Arrange
        conBearerForzado("principal", identidadBearer);
        when(aplicadorDecision.aplicar(request, response, "principal", "tok-1")).thenReturn(true);

        // Act
        filtro().doFilter(request, response, cadena);

        // Assert
        verify(aplicadorDecision).aplicar(request, response, "principal", "tok-1");
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeResponder401SinConsultarAlPdp_cuandoElEmisorForzadoNoCoincideConElToken() throws Exception {
        // Arrange
        lenient().when(bearer.emisorHabilitado("legado")).thenReturn(Optional.of("legado"));
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-1"));
        var fallo = new BadCredentialsException("emisor");
        when(bearer.autenticar("tok-1", "legado")).thenThrow(fallo);
        request.addHeader(SeleccionProveedor.CABECERA, "legado");

        // Act
        filtro().doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(request, response, fallo);
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
        verify(cadena, never()).doFilter(any(), any());
    }
}
