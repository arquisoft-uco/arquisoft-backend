package com.arquisoft.seguridad.infrastructure.web;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.infrastructure.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.infrastructure.exception.TokenInvalidoException;
import com.arquisoft.shared.web.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeguridadGlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private SeguridadGlobalExceptionHandler handler;

    @Test
    void debeRetornar401_cuandoCredencialesInvalidas() {
        // Arrange
        CredencialesInvalidasException exception = new CredencialesInvalidasException("Credenciales incorrectas");
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        // Act
        ResponseEntity<ErrorResponseDTO> response = handler.handleCredencialesInvalidas(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("No autorizado");
        assertThat(response.getBody().getErrorCode()).isEqualTo("CREDENCIALES_INVALIDAS");
        assertThat(response.getBody().getMessage()).contains("Credenciales incorrectas");
        assertThat(response.getBody().getPath()).isEqualTo("/api/auth/login");
    }

    @Test
    void debeRetornar401_cuandoTokenInvalido() {
        // Arrange
        TokenInvalidoException exception = new TokenInvalidoException("Refresh token expirado");
        when(request.getRequestURI()).thenReturn("/api/auth/refresh");

        // Act
        ResponseEntity<ErrorResponseDTO> response = handler.handleTokenInvalido(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("No autorizado");
        assertThat(response.getBody().getErrorCode()).isEqualTo("TOKEN_INVALIDO");
        assertThat(response.getBody().getMessage()).contains("Refresh token expirado");
        assertThat(response.getBody().getPath()).isEqualTo("/api/auth/refresh");
    }

    @Test
    void debeRetornar401_cuandoAuthenticationException() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Error de autenticacion generico");
        when(request.getRequestURI()).thenReturn("/api/auth/validate");

        // Act
        ResponseEntity<ErrorResponseDTO> response = handler.handleAutenticacion(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError()).isEqualTo("No autorizado");
        assertThat(response.getBody().getErrorCode()).isEqualTo("AUTENTICACION_ERROR");
        assertThat(response.getBody().getMessage()).contains("Error de autenticacion generico");
        assertThat(response.getBody().getPath()).isEqualTo("/api/auth/validate");
    }
}
