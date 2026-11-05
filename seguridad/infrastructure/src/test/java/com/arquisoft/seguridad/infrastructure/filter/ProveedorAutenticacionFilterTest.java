package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.config.cors.OrigenesPermitidos;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.ProteccionCsrf;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProveedorAutenticacionFilterTest {

    private static final String COOKIE = "SECURITY_BASELINE_SESSION=abc";
    private static final String ORIGEN = "http://localhost:3000";

    @Mock
    private ProveedorAutenticacion proveedor;

    @Mock
    private AplicadorDecisionAcceso aplicador;

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

    private ProveedorAutenticacionFilter filtro;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void preparar() {
        lenient().when(proveedor.id()).thenReturn("sesion-pdp");
        lenient().when(proveedor.habilitado()).thenReturn(true);
        var rutas = new RutasPublicas("/api", List.of("/actuator/health/**"), List.of("/auth/logout"), "none",
                new MockEnvironment());
        filtro = new ProveedorAutenticacionFilter(new ProveedoresAutenticacion(List.of(proveedor)),
                new ProteccionCsrf(new OrigenesPermitidos(ORIGEN)), aplicador, rutas, entryPoint,
                accesoDenegadoHandler, resolvedorExcepciones, appLogger);
        request = new MockHttpServletRequest("POST", "/api/fichas-perfil");
        request.setContextPath("/api");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private void conCookieYCsrfValido() {
        when(proveedor.extraerCredencial(request)).thenReturn(Optional.of(COOKIE));
        when(proveedor.exigeProteccionCsrf()).thenReturn(true);
        request.addHeader("X-XSRF-TOKEN", "tok");
        request.addHeader("Origin", ORIGEN);
    }

    private void conBearerEnElContexto() {
        var jwt = Jwt.withTokenValue("t").header("alg", "RS256").subject("sub-jwt")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(), "sub-jwt"));
    }

    @Test
    void debeOmitir_cuandoPreflightPublicaTecnicaOExenta() throws Exception {
        // Arrange
        var preflight = new MockHttpServletRequest("OPTIONS", "/api/fichas-perfil");
        preflight.addHeader("Origin", ORIGEN);
        preflight.addHeader("Access-Control-Request-Method", "POST");
        var publica = new MockHttpServletRequest("GET", "/api/actuator/health/liveness");
        var tecnica = new MockHttpServletRequest("GET", "/api/actuator/prometheus");
        var exenta = new MockHttpServletRequest("POST", "/api/auth/logout");
        for (var peticion : List.of(publica, tecnica, exenta)) {
            peticion.setContextPath("/api");
        }

        // Act
        filtro.doFilter(preflight, new MockHttpServletResponse(), cadena);
        filtro.doFilter(publica, new MockHttpServletResponse(), cadena);
        filtro.doFilter(tecnica, new MockHttpServletResponse(), cadena);
        filtro.doFilter(exenta, new MockHttpServletResponse(), cadena);

        // Assert
        verify(cadena).doFilter(eq(preflight), any());
        verify(cadena).doFilter(eq(exenta), any());
        verifyNoInteractions(aplicador, entryPoint);
        verify(proveedor, never()).extraerCredencial(any());
    }

    @Test
    void debeContinuar_cuandoNoActua() throws Exception {
        // Arrange
        conBearerEnElContexto();

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(cadena).doFilter(request, response);
        verifyNoInteractions(aplicador, entryPoint);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isInstanceOf(JwtAuthenticationToken.class);
    }

    @Test
    void debeResponder401_cuandoSeleccionRechazada() throws Exception {
        // Arrange
        request.addHeader(SeleccionProveedor.CABECERA, "desconocido");

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(request), eq(response), any(InsufficientAuthenticationException.class));
        verify(appLogger).warn(ResolucionSesionKey.LOG_SELECCION_RECHAZADA, SeleccionProveedor.MOTIVO_DESCONOCIDO);
        verify(cadena, never()).doFilter(any(), any());
        verifyNoInteractions(aplicador);
    }

    @Test
    void debeResponder403SinLlamarAlPep_cuandoCsrfInvalido() throws Exception {
        // Arrange
        when(proveedor.extraerCredencial(request)).thenReturn(Optional.of(COOKIE));
        when(proveedor.exigeProteccionCsrf()).thenReturn(true);
        request.addHeader("Origin", ORIGEN);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(accesoDenegadoHandler).handle(eq(request), eq(response), any(AccessDeniedException.class));
        verify(appLogger).warn(ResolucionSesionKey.LOG_CSRF_RECHAZADO, "POST", "SIN_CABECERA_XSRF");
        verifyNoInteractions(aplicador);
        verify(proveedor, never()).autenticar(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeCortarSinResolverIdentidad_cuandoElPepNoPermite() throws Exception {
        // Arrange
        conCookieYCsrfValido();
        when(aplicador.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(false);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(proveedor, never()).autenticar(anyString());
        verify(cadena, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void debeAutenticar_cuandoElPepPermite() throws Exception {
        // Arrange
        conCookieYCsrfValido();
        var token = new SesionAuthenticationToken("sesion-pdp", "sub-1", "Ana", "a@uco.edu.co", "t");
        when(aplicador.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        when(proveedor.autenticar(COOKIE)).thenReturn(token);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(token);
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeReemplazarElBearer_cuandoSeForzaLaSesion() throws Exception {
        // Arrange
        conBearerEnElContexto();
        request.addHeader(SeleccionProveedor.CABECERA, "sesion-pdp");
        conCookieYCsrfValido();
        var token = new SesionAuthenticationToken("sesion-pdp", "sub-1", "Ana", "a@uco.edu.co", "t");
        when(aplicador.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        when(proveedor.autenticar(COOKIE)).thenReturn(token);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(token);
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeResolver503_cuandoPdpDeSesionNoDisponible() throws Exception {
        // Arrange
        conCookieYCsrfValido();
        var fallo = new InfrastructureException("pdp caido", "SEG-X");
        when(aplicador.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        when(proveedor.autenticar(COOKIE)).thenThrow(fallo);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
        verify(cadena, never()).doFilter(any(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void debeResponder401_cuandoElProveedorLanzaAutenticacionFallida() throws Exception {
        // Arrange
        conCookieYCsrfValido();
        when(aplicador.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        when(proveedor.autenticar(COOKIE)).thenThrow(new InsufficientAuthenticationException("sin sesion"));

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(request), eq(response), any(InsufficientAuthenticationException.class));
        verify(cadena, never()).doFilter(any(), any());
    }
}
