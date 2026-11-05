package com.arquisoft.seguridad.infrastructure.filter;

import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionAuthenticationToken;
import com.arquisoft.shared.tracing.application.traza.primaryport.GestorTraza;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IdentidadTrazaFilterTest {

    @Mock
    private GestorTraza gestorTraza;

    @Mock
    private FilterChain cadena;

    @AfterEach
    void limpiar() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void debeRegistrarSujetoDelJwt_cuandoHayJwt() throws Exception {
        // Arrange
        var jwt = Jwt.withTokenValue("t").header("alg", "RS256").subject("sub-jwt")
                .issuedAt(Instant.now()).expiresAt(Instant.now().plusSeconds(60)).build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(), "sub-jwt"));
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        // Act
        new IdentidadTrazaFilter(gestorTraza).doFilter(request, response, cadena);

        // Assert
        verify(gestorTraza).registrarUsuario("sub-jwt");
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeRegistrarSujetoDeLaSesion_cuandoHaySesion() throws Exception {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(
                new SesionAuthenticationToken("sesion-pdp", "sub-sesion", "Ana", "a@uco.edu.co", "t"));
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        // Act
        new IdentidadTrazaFilter(gestorTraza).doFilter(request, response, cadena);

        // Assert
        verify(gestorTraza).registrarUsuario("sub-sesion");
        verify(cadena).doFilter(request, response);
    }

    @Test
    void debeContinuarSinRegistrar_cuandoNoHayAutenticacion() throws Exception {
        // Arrange
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        // Act
        new IdentidadTrazaFilter(gestorTraza).doFilter(request, response, cadena);

        // Assert
        verify(gestorTraza, never()).registrarUsuario(anyString());
        verify(cadena).doFilter(request, response);
    }
}
