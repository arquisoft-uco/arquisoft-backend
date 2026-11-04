package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.application.autorizacion.command.primaryport.interactor.AutorizarAccesoInteractor;
import com.arquisoft.seguridad.application.autorizacion.command.primaryport.model.AutorizarAccesoCommand;
import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web.ResolvedorPlantillaRuta;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.security.AccesoDenegadoHandler;
import com.arquisoft.seguridad.infrastructure.config.security.NoAutenticadoEntryPoint;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AutorizacionAccesoFilterTest {

    private static final String ISSUER = "https://idp.example.com/realms/test";

    @Mock
    private AutorizarAccesoInteractor interactor;

    @Mock
    private ResolvedorPlantillaRuta resolvedor;

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private NoAutenticadoEntryPoint entryPoint;

    @Mock
    private AccesoDenegadoHandler accesoDenegadoHandler;

    @Mock
    private HandlerExceptionResolver resolvedorExcepciones;

    @Mock
    private FilterChain cadena;

    private AutorizacionAccesoFilter filtro;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void preparar() {
        var emisor = new AutenticacionProperties.Emisor("principal", true, ISSUER, "", List.of(),
                ModoAutorizacion.PEP, false, new ClaimsIdentidad("sub", "email", "preferred_username", "name", ""));
        var rutas = new RutasPublicas("/api", List.of("/actuator/health/**"), List.of("/auth/logout"), "none",
                new MockEnvironment());
        filtro = new AutorizacionAccesoFilter(interactor, resolvedor, gestorTraza, rutas,
                new AutenticacionProperties(List.of(emisor)), entryPoint, accesoDenegadoHandler, resolvedorExcepciones);
        request = new MockHttpServletRequest("GET", "/api/fichas-perfil/123");
        request.setContextPath("/api");
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarCon(String issuer) {
        var jwt = Jwt.withTokenValue("tok-1").header("alg", "RS256").subject("sub-1").issuer(issuer)
                .issuedAt(Instant.now().minusSeconds(10)).expiresAt(Instant.now().plusSeconds(600)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(), "sub-1"));
    }

    private void stubearComando() {
        when(resolvedor.identificarRecurso(request)).thenReturn("/api/fichas-perfil/{id}");
        when(gestorTraza.correlacionActual()).thenReturn("corr-1");
    }

    @Test
    void debeOmitirLaDecision_cuandoPreflightRutaPublicaORutaExenta() throws Exception {
        // Arrange
        var preflight = new MockHttpServletRequest("OPTIONS", "/api/fichas-perfil");
        preflight.addHeader("Origin", "http://localhost:3000");
        preflight.addHeader("Access-Control-Request-Method", "GET");
        var publica = new MockHttpServletRequest("GET", "/api/actuator/health/liveness");
        var exenta = new MockHttpServletRequest("POST", "/api/auth/logout");

        // Act
        filtro.doFilter(preflight, new MockHttpServletResponse(), cadena);
        filtro.doFilter(publica, new MockHttpServletResponse(), cadena);
        filtro.doFilter(exenta, new MockHttpServletResponse(), cadena);

        // Assert
        verify(cadena).doFilter(eq(preflight), any());
        verify(cadena).doFilter(eq(publica), any());
        verify(cadena).doFilter(eq(exenta), any());
        verify(interactor, never()).ejecutar(any());
    }

    @Test
    void debeResponder401SinConsultar_cuandoNoHayAutenticacionOElEmisorEsDesconocido() throws Exception {
        // Arrange
        var sinAutenticacion = new MockHttpServletRequest("GET", "/api/fichas-perfil");

        // Act
        filtro.doFilter(sinAutenticacion, response, cadena);
        autenticarCon("https://otro-emisor");
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(sinAutenticacion), any(), any(InsufficientAuthenticationException.class));
        verify(entryPoint).commence(eq(request), any(), any(InsufficientAuthenticationException.class));
        verify(interactor, never()).ejecutar(any());
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeContinuarConXDecisionId_cuandoPermitida() throws Exception {
        // Arrange
        autenticarCon(ISSUER);
        stubearComando();
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida("dec-5"));
        var captor = ArgumentCaptor.forClass(AutorizarAccesoCommand.class);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(cadena).doFilter(request, response);
        assertThat(response.getHeader("X-Decision-Id")).isEqualTo("dec-5");
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().metodo()).isEqualTo("GET");
        assertThat(captor.getValue().recurso()).isEqualTo("/api/fichas-perfil/{id}");
        assertThat(captor.getValue().emisor()).isEqualTo("principal");
        assertThat(captor.getValue().credencial()).isEqualTo("tok-1");
        assertThat(captor.getValue().correlacion()).isEqualTo("corr-1");
    }

    @Test
    void debeNoAgregarXDecisionId_cuandoLaDecisionNoTraeId() throws Exception {
        // Arrange
        autenticarCon(ISSUER);
        stubearComando();
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Permitida(""));

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(cadena).doFilter(request, response);
        assertThat(response.getHeader("X-Decision-Id")).isNull();
    }

    @Test
    void debeResponder401_cuandoNoAutenticada() throws Exception {
        // Arrange
        autenticarCon(ISSUER);
        stubearComando();
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.NoAutenticada());

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(entryPoint).commence(eq(request), eq(response), any(InsufficientAuthenticationException.class));
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeResponder403_cuandoDenegada() throws Exception {
        // Arrange
        autenticarCon(ISSUER);
        stubearComando();
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenReturn(new DecisionAccesoResult.Denegada());

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(accesoDenegadoHandler).handle(eq(request), eq(response), any(AccessDeniedException.class));
        verify(cadena, never()).doFilter(any(), any());
    }

    @Test
    void debeDelegarEnElResolvedorDeExcepciones_cuandoElProveedorNoEstaDisponible() throws Exception {
        // Arrange
        autenticarCon(ISSUER);
        stubearComando();
        var fallo = new ProveedorAutorizacionNoDisponibleException("pep caido");
        when(interactor.ejecutar(any(AutorizarAccesoCommand.class))).thenThrow(fallo);

        // Act
        filtro.doFilter(request, response, cadena);

        // Assert
        verify(resolvedorExcepciones).resolveException(request, response, null, fallo);
        verify(cadena, never()).doFilter(any(), any());
    }
}
