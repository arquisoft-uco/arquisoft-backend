package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.security.RutasPublicas;
import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import tools.jackson.databind.ObjectMapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtListaNegraFilterTest {

    private static final String JTI = "jti-123";

    private static final String ISSUER = "https://idp.example.com/realms/test";

    @Mock
    private RutasPublicas rutasPublicas;

    @Mock
    private TokenInvalidadoOutputPort tokenInvalidadoPort;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AppLogger logger;

    private JwtListaNegraFilter filter;

    @BeforeEach
    void crearFiltro() {
        filter = filtroConListaNegra(true);
    }

    private JwtListaNegraFilter filtroConListaNegra(boolean blacklist) {
        var emisor = new AutenticacionProperties.Emisor("principal", true, ISSUER, "", List.of(),
                ModoAutorizacion.PEP, blacklist, new ClaimsIdentidad("sub", "email", "preferred_username", "name", "", ""));
        return new JwtListaNegraFilter(logger, rutasPublicas, new AutenticacionProperties(List.of(emisor)),
                tokenInvalidadoPort, objectMapper);
    }

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarConJti(String jti) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
                .issuer(ISSUER)
                .jti(jti)
                .issuedAt(Instant.now().minusSeconds(60))
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new JwtAuthenticationToken(jwt, List.of()));
    }

    @Test
    void debeRetornar401_cuandoElTokenFueRevocadoEnLogout() throws Exception {
        // Arrange — mismo token usado para logout, ahora en la blacklist
        autenticarConJti(JTI);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getRequestURI()).thenReturn("/api/fichas-perfil");
        when(tokenInvalidadoPort.estaInvalidado(JTI)).thenReturn(true);
        when(response.getWriter()).thenReturn(new PrintWriter(new StringWriter()));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(401);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding(StandardCharsets.UTF_8.name());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void debeContinuar_cuandoElTokenNoEstaEnLaBlacklist() throws Exception {
        // Arrange
        autenticarConJti(JTI);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(tokenInvalidadoPort.estaInvalidado(JTI)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(401);
    }

    @Test
    void debeNoFiltrar_cuandoLaRutaEsPublicaOTecnica() {
        // Arrange
        var publica = new MockHttpServletRequest("POST", "/api/auth/login");
        var tecnica = new MockHttpServletRequest("GET", "/api/actuator/health");
        var protegida = new MockHttpServletRequest("GET", "/api/fichas-perfil");
        when(rutasPublicas.esPublica("/api/auth/login")).thenReturn(true);
        when(rutasPublicas.esPublica("/api/fichas-perfil")).thenReturn(false);

        // Act / Assert
        assertThat(filter.shouldNotFilter(publica)).isTrue();
        assertThat(filter.shouldNotFilter(protegida)).isFalse();
        assertThat(filter.shouldNotFilter(tecnica)).isTrue();
    }

    @Test
    void debeOmitirLaConsulta_cuandoElEmisorNoUsaListaNegra() throws Exception {
        // Arrange
        filter = filtroConListaNegra(false);
        autenticarConJti(JTI);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(tokenInvalidadoPort, never()).estaInvalidado(JTI);
        verify(filterChain).doFilter(request, response);
    }
}
