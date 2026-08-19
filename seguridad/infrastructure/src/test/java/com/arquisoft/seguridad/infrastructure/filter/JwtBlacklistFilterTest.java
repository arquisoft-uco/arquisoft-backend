package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtBlacklistFilterTest {

    private static final String JTI = "jti-123";

    @Mock
    private TokenInvalidadoOutputPort tokenInvalidadoPort;

    @Mock
    private ObjectMapper objectMapper;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
@InjectMocks
    private JwtBlacklistFilter filter;

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private void autenticarConJti(String jti) {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
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
        verify(response).setContentType("application/json;charset=UTF-8");
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
}
