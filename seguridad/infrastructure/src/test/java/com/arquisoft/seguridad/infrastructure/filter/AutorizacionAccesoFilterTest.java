package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.autenticacion.command.primaryadapter.web.AplicadorListaNegra;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.AplicadorDecisionAcceso;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.BearerAuthenticationToken;
import com.arquisoft.seguridad.infrastructure.autenticacion.exception.UsuariosNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SeleccionProveedor;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacionAccesoFilterTest {

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

    private ProveedorAutenticacion bearer;
    private ProveedorAutenticacion sesion;
    private BearerAuthenticationToken identidadBearer;
    private SesionAuthenticationToken identidadSesion;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void preparar() {
        bearer = mock(ProveedorAutenticacion.class);
        lenient().when(bearer.id()).thenReturn("bearer");
        lenient().when(bearer.habilitado()).thenReturn(true);
        lenient().when(bearer.autenticaPrimero()).thenReturn(true);
        lenient().when(bearer.actuaEnRutasNoDeNegocio()).thenReturn(true);
        lenient().when(bearer.extraerCredencial(any())).thenReturn(Optional.empty());
        sesion = mock(ProveedorAutenticacion.class);
        lenient().when(sesion.id()).thenReturn("sesion-pdp");
        lenient().when(sesion.habilitado()).thenReturn(true);
        lenient().when(sesion.exigeProteccionCsrf()).thenReturn(true);
        lenient().when(sesion.autenticaPrimero()).thenReturn(false);
        lenient().when(sesion.actuaEnRutasNoDeNegocio()).thenReturn(false);
        lenient().when(sesion.extraerCredencial(any())).thenReturn(Optional.empty());
        identidadBearer = new BearerAuthenticationToken("principal", "sub-1", null, null, List.of(), "jti-1", null);
        identidadSesion = new SesionAuthenticationToken("sesion-pdp", "sub-2", "Ana", "ana@uco.edu.co", "t");
        lenient().when(aplicadorListaNegra.aplicar(any(), any(), any())).thenReturn(true);
        request = new MockHttpServletRequest("GET", "/api/fichas-perfil/123");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private AutorizacionAccesoFilter filtro(boolean decisionHabilitada) {
        var rutas = new RutasPublicas("/api", List.of("/actuator/health/**"), List.of("/auth/logout"), "none",
                new MockEnvironment());
        return new AutorizacionAccesoFilter(new ProveedoresAutenticacion(List.of(bearer, sesion)), proteccionCsrf,
                aplicadorDecision, aplicadorListaNegra, gestorTraza, rutas, entryPoint, accesoDenegadoHandler,
                resolvedorExcepciones, appLogger, decisionHabilitada);
    }

    private void conBearerValido() {
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-1"));
        when(bearer.autenticar("tok-1")).thenReturn(identidadBearer);
    }

    private void conCookieValida() {
        lenient().when(sesion.extraerCredencial(any())).thenReturn(Optional.of(COOKIE));
        lenient().when(sesion.autenticar(COOKIE)).thenReturn(identidadSesion);
    }

    @Test
    void debeOmitir_cuandoPreflightORutaPublica() throws Exception {
        // Arrange
        var preflight = new MockHttpServletRequest("OPTIONS", "/api/fichas-perfil");
        preflight.addHeader("Origin", "http://localhost:3000");
        preflight.addHeader("Access-Control-Request-Method", "GET");
        var publica = new MockHttpServletRequest("GET", "/api/actuator/health/liveness");

        // Act
        filtro(true).doFilter(preflight, new MockHttpServletResponse(), cadena);
        filtro(true).doFilter(publica, new MockHttpServletResponse(), cadena);

        // Assert
        verify(cadena).doFilter(eq(preflight), any());
        verify(cadena).doFilter(eq(publica), any());
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
    }

    @Test
    void debeResponder401_cuandoNoHayCredencial() throws Exception {
        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(request), any(), any(InsufficientAuthenticationException.class));
        verify(cadena, never()).doFilter(any(), any());
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
    }

    @Test
    void debeResponder401_cuandoSeleccionRechazada() throws Exception {
        // Arrange
        request.addHeader(SeleccionProveedor.CABECERA, "desconocido");

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(request), any(), any(InsufficientAuthenticationException.class));
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeResponder401SinDecidir_cuandoBearerInvalido() throws Exception {
        // Arrange
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-malo"));
        var fallo = new BadCredentialsException("invalido");
        when(bearer.autenticar("tok-malo")).thenThrow(fallo);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(request, response, fallo);
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeResponder401_cuandoLaCabeceraBearerEstaMalformada() throws Exception {
        // Arrange
        var fallo = new BadCredentialsException("malformada");
        when(bearer.extraerCredencial(any())).thenThrow(fallo);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(request, response, fallo);
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
    }

    @Test
    void debeAutenticarYDecidirConElEmisorDelToken_cuandoBearerValido() throws Exception {
        // Arrange
        conBearerValido();
        when(aplicadorDecision.aplicar(request, response, "principal", "tok-1")).thenReturn(true);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(cadena).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(identidadBearer);
    }

    @Test
    void debeRegistrarElSujetoEnLaTrazaAntesDeDecidir_cuandoBearer() throws Exception {
        // Arrange
        conBearerValido();
        when(aplicadorDecision.aplicar(request, response, "principal", "tok-1")).thenReturn(false);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        InOrder orden = inOrder(aplicadorListaNegra, gestorTraza, aplicadorDecision);
        orden.verify(aplicadorListaNegra).aplicar(request, response, identidadBearer);
        orden.verify(gestorTraza).registrarUsuario("sub-1");
        orden.verify(aplicadorDecision).aplicar(request, response, "principal", "tok-1");
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeCortarSinDecidir_cuandoLaListaNegraRechaza() throws Exception {
        // Arrange
        conBearerValido();
        when(aplicadorListaNegra.aplicar(request, response, identidadBearer)).thenReturn(false);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
        verify(gestorTraza, never()).registrarUsuario(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeDelegarAlResolvedor_cuandoJwksNoEstaDisponible() throws Exception {
        // Arrange
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-1"));
        var fallo = new AuthenticationServiceException("jwks caido");
        when(bearer.autenticar("tok-1")).thenThrow(fallo);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
        verify(entryPoint, never()).commence(any(), any(), any());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeAutenticarSinDecidir_cuandoBearerEnRutaExenta() throws Exception {
        // Arrange
        var logout = new MockHttpServletRequest("POST", "/api/auth/logout");
        when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-1"));
        when(bearer.autenticar("tok-1")).thenReturn(identidadBearer);

        // Act
        filtro(true).doFilter(logout, response, cadena);

        // Assert
        verify(cadena).doFilter(logout, response);
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
    }

    @Test
    void debeResponder401_cuandoSoloCookieEnRutaExenta() throws Exception {
        // Arrange
        var logout = new MockHttpServletRequest("POST", "/api/auth/logout");
        conCookieValida();

        // Act
        filtro(true).doFilter(logout, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(logout), any(), any(InsufficientAuthenticationException.class));
        verify(sesion, never()).autenticar(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeAutenticarSinDecidir_cuandoSecurityDeshabilitado() throws Exception {
        // Arrange
        conBearerValido();

        // Act
        filtro(false).doFilter(request, response, cadena);

        // Assert
        verify(cadena).doFilter(request, response);
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(identidadBearer);
    }

    @Test
    void debeResponder403SinDecidir_cuandoCsrfInvalido() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.SIN_CABECERA_XSRF);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(accesoDenegadoHandler).handle(eq(request), eq(response), any(AccessDeniedException.class));
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
        verify(sesion, never()).autenticar(anyString());
    }

    @Test
    void debeDecidirAntesDeAutenticar_cuandoCookie() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        InOrder orden = inOrder(aplicadorDecision, sesion, gestorTraza);
        orden.verify(aplicadorDecision).aplicar(request, response, "sesion-pdp", COOKIE);
        orden.verify(sesion).autenticar(COOKIE);
        orden.verify(gestorTraza).registrarUsuario("sub-2");
        verify(cadena).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(identidadSesion);
    }

    @Test
    void debeDelegarAlResolvedor403_cuandoUsuarioNoRegistrado() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        var fallo = new AccessDeniedException("sin usuario");
        when(sesion.autenticar(COOKIE)).thenThrow(fallo);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
        verify(gestorTraza, never()).registrarUsuario(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeDelegarAlResolvedor503_cuandoUsuariosNoDisponible() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        var fallo = new UsuariosNoDisponibleException("usuarios caido", null);
        when(sesion.autenticar(COOKIE)).thenThrow(fallo);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
        verify(entryPoint, never()).commence(any(), any(), any());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeCortarSinResolverIdentidad_cuandoElPepNoPermiteCookie() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(false);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(sesion, never()).autenticar(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeGanarBearer_cuandoBearerYCookieSinEncabezado() throws Exception {
        // Arrange
        conBearerValido();
        lenient().when(sesion.extraerCredencial(any())).thenReturn(Optional.of(COOKIE));
        when(aplicadorDecision.aplicar(request, response, "principal", "tok-1")).thenReturn(true);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(bearer).autenticar("tok-1");
        verify(sesion, never()).autenticar(anyString());
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeForzarSesion_cuandoElEncabezadoLaNombra() throws Exception {
        // Arrange
        request.addHeader(SeleccionProveedor.CABECERA, "sesion-pdp");
        conCookieValida();
        lenient().when(bearer.extraerCredencial(any())).thenReturn(Optional.of("tok-1"));
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(bearer, never()).autenticar(anyString());
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeAutenticarSinDecidir_cuandoCookieYSecurityDeshabilitado() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);

        // Act
        filtro(false).doFilter(request, response, cadena);

        // Assert
        verify(aplicadorDecision, never()).aplicar(any(), any(), anyString(), anyString());
        verify(gestorTraza).registrarUsuario("sub-2");
        verify(cadena).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(identidadSesion);
    }

    @Test
    void debeCortarSinRegistrarUsuario_cuandoLaListaNegraRechazaLaSesion() throws Exception {
        // Arrange
        conCookieValida();
        when(proteccionCsrf.evaluar(any(), any(), any(), any())).thenReturn(ProteccionCsrf.Resultado.PERMITIDA);
        when(aplicadorDecision.aplicar(request, response, "sesion-pdp", COOKIE)).thenReturn(true);
        when(aplicadorListaNegra.aplicar(request, response, identidadSesion)).thenReturn(false);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(gestorTraza, never()).registrarUsuario(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeDelegarAlResolvedor_cuandoLaSeleccionFallaConUnErrorInesperado() throws Exception {
        // Arrange
        var fallo = new IllegalStateException("fallo inesperado");
        when(bearer.extraerCredencial(any())).thenThrow(fallo);

        // Act
        filtro(true).doFilter(request, response, cadena);

        // Assert
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
        verify(entryPoint, never()).commence(any(), any(), any());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeIgnorarLaCookie_cuandoLaRutaEsTecnica() throws Exception {
        // Arrange
        var swagger = new MockHttpServletRequest("GET", "/api/swagger-ui/index.html");
        conCookieValida();

        // Act
        filtro(true).doFilter(swagger, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(swagger), any(), any(InsufficientAuthenticationException.class));
        verify(sesion, never()).autenticar(anyString());
        verify(cadena, never()).doFilter(any(), any());
    }
}
