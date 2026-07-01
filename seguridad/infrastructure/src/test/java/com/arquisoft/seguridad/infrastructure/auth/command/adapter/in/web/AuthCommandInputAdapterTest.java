package com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web;

import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.port.in.AuthenticateUserInputPort;
import com.arquisoft.seguridad.application.auth.command.port.in.LogoutInputPort;
import com.arquisoft.seguridad.application.auth.command.port.in.RefreshTokenInputPort;
import com.arquisoft.seguridad.application.auth.command.port.in.ValidateTokenInputPort;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenAggregate;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LoginRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LoginResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.ValidateTokenResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthCommandInputAdapterTest {

    @Mock
    private AuthenticateUserInputPort authenticateUserInputPort;

    @Mock
    private RefreshTokenInputPort refreshTokenInputPort;

    @Mock
    private LogoutInputPort logoutInputPort;

    @Mock
    private ValidateTokenInputPort validateTokenInputPort;

    @InjectMocks
    private AuthCommandInputAdapter adapter;

    @Test
    void debeRetornar200_cuandoLoginExitoso() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .email("estudiante@uco.edu.co")
                .password("password123")
                .build();

        AuthenticateUserInputPort.AuthResult authResult = new AuthenticateUserInputPort.AuthResult(
                "eyJhbGc-access...",
                "eyJhbGc-refresh...",
                3600L,
                "Bearer",
                "openid profile email"
        );

        when(authenticateUserInputPort.ejecutar(any())).thenReturn(authResult);

        // Act
        ResponseEntity<LoginResponseDTO> response = adapter.login(request);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("eyJhbGc-access...");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("eyJhbGc-refresh...");
        assertThat(response.getBody().getExpiresIn()).isEqualTo(3600L);
        verify(authenticateUserInputPort).ejecutar(any());
    }

    @Test
    void debeRetornar200_cuandoRefreshExitoso() {
        // Arrange
        RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder()
                .refreshToken("eyJhbGc-refresh-old...")
                .build();

        RefreshTokenInputPort.RefreshResult refreshResult = new RefreshTokenInputPort.RefreshResult(
                "eyJhbGc-access-new...",
                "eyJhbGc-refresh-new...",
                3600L,
                "Bearer",
                "openid profile email"
        );

        when(refreshTokenInputPort.ejecutar(anyString())).thenReturn(refreshResult);

        // Act
        ResponseEntity<LoginResponseDTO> response = adapter.refreshToken(request);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("eyJhbGc-access-new...");
        verify(refreshTokenInputPort).ejecutar("eyJhbGc-refresh-old...");
    }

    @Test
    void debeRetornar200_cuandoLogoutConJwtValido() {
        // Arrange
        Jwt jwt = Jwt.withTokenValue("token-prueba")
                .header("alg", "RS256")
                .subject("uuid-estudiante")
                .jti("jti-123")
                .expiresAt(Instant.now().plusSeconds(3600))
                .issuedAt(Instant.now())
                .build();

        // Act
        ResponseEntity<LogoutResponseDTO> response = adapter.logout(jwt);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(logoutInputPort).ejecutar(any(TokenSesionCommand.class));
    }

    @Test
    void debeRetornar200_cuandoValidateToken() {
        // Arrange
        ValidateTokenInputPort.ValidationResult validationResult =
                new ValidateTokenInputPort.ValidationResult(
                        true,
                        "uuid-estudiante-123",
                        "estudiante@uco.edu.co",
                        "Token valido"
                );

        when(validateTokenInputPort.ejecutar(any())).thenReturn(validationResult);

        // Act
        ResponseEntity<ValidateTokenResponseDTO> response = adapter.validateToken("eyJhbGc-token-valido...");

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isTrue();
        assertThat(response.getBody().getIdentidadId()).isEqualTo("uuid-estudiante-123");
        assertThat(response.getBody().getCorreo()).isEqualTo("estudiante@uco.edu.co");
        verify(validateTokenInputPort).ejecutar(any(TokenAggregate.class));
    }
}
