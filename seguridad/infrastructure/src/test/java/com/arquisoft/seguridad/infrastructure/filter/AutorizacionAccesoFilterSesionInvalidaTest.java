package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.AplicadorListaNegra;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacionAccesoFilterSesionInvalidaTest {

    private static final String COOKIE = "SECURITY_BASELINE_SESSION=abc";

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

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeResponder401SinContinuar_cuandoLaCookieEsInvalidaAunqueLaDecisionSeaPermitida() throws Exception {
        // Arrange
        var bearer = mock(ProveedorAutenticacion.class);
        lenient().when(bearer.id()).thenReturn("bearer");
        lenient().when(bearer.habilitado()).thenReturn(true);
        lenient().when(bearer.extraerCredencial(any())).thenReturn(Optional.empty());
        var sesion = mock(ProveedorAutenticacion.class);
        lenient().when(sesion.id()).thenReturn("sesion-pdp");
        lenient().when(sesion.habilitado()).thenReturn(true);
        lenient().when(sesion.exigeProteccionCsrf()).thenReturn(true);
        lenient().when(sesion.autenticaPrimero()).thenReturn(false);
        lenient().when(sesion.extraerCredencial(any())).thenReturn(Optional.of(COOKIE));
        var fallo = new BadCredentialsException("sesion vencida");
        when(sesion.autenticar(COOKIE, SeleccionProveedor.SIN_EMISOR)).thenThrow(fallo);
        var request = new MockHttpServletRequest("GET", "/api/usuarios/mi/perfil");
        var response = new MockHttpServletResponse();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        var rutas = new RutasPublicas("/api", List.of("/actuator/health/**"), List.of("/auth/logout"), "none",
                new MockEnvironment());
        var filtro = new AutorizacionAccesoFilter(new ProveedoresAutenticacion(List.of(bearer, sesion)),
                proteccionCsrf, aplicadorDecision, aplicadorListaNegra, gestorTraza, rutas, entryPoint,
                accesoDenegadoHandler, resolvedorExcepciones, appLogger, true);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(request), any(), eq(fallo));
        verify(gestorTraza, never()).registrarUsuario(anyString());
        verify(cadena, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
