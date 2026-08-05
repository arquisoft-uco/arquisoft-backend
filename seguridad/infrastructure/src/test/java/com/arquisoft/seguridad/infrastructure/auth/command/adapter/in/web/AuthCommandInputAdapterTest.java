package com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.seguridad.application.auth.command.interactor.AuthenticateUserInteractor;
import com.arquisoft.seguridad.application.auth.command.interactor.LogoutInteractor;
import com.arquisoft.seguridad.application.auth.command.interactor.RefreshTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.interactor.ValidateTokenInteractor;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.result.AutenticacionResult;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.application.auth.command.result.ValidacionTokenResult;
import com.arquisoft.seguridad.domain.auth.aggregate.TokenDomain;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LoginRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LoginResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.LogoutResponseDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.infrastructure.auth.command.adapter.in.web.dto.ValidateTokenResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
    private AuthenticateUserInteractor authenticateUserInteractor;

    @Mock
    private RefreshTokenInteractor refreshTokenInteractor;

    @Mock
    private LogoutInteractor logoutInteractor;

    @Mock
    private ValidateTokenInteractor validateTokenInteractor;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private AuthCommandInputAdapter adapter;

    @Test
    void debeRetornar200_cuandoLoginExitoso() {
        // Arrange
        LoginRequestDTO request = new LoginRequestDTO("estudiante@uco.edu.co", "password123");

        AutenticacionResult authResult = new AutenticacionResult(
                "eyJhbGc-access...",
                "eyJhbGc-refresh...",
                3600L,
                "Bearer",
                "openid profile email"
        );

        when(authenticateUserInteractor.ejecutar(any())).thenReturn(authResult);

        // Act
        ResponseEntity<LoginResponseDTO> response = adapter.login(request);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("eyJhbGc-access...");
        assertThat(response.getBody().getRefreshToken()).isEqualTo("eyJhbGc-refresh...");
        assertThat(response.getBody().getExpiresIn()).isEqualTo(3600L);
        verify(authenticateUserInteractor).ejecutar(any());
    }

    @Test
    void debeRetornar200_cuandoRefreshExitoso() {
        // Arrange
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("eyJhbGc-refresh-old...");

        RefrescoTokenResult refreshResult = new RefrescoTokenResult(
                "eyJhbGc-access-new...",
                "eyJhbGc-refresh-new...",
                3600L,
                "Bearer",
                "openid profile email"
        );

        when(refreshTokenInteractor.ejecutar(anyString())).thenReturn(refreshResult);

        // Act
        ResponseEntity<LoginResponseDTO> response = adapter.refreshToken(request);

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isEqualTo("eyJhbGc-access-new...");
        verify(refreshTokenInteractor).ejecutar("eyJhbGc-refresh-old...");
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
        verify(logoutInteractor).ejecutar(any(TokenSesionCommand.class));
    }

    @Test
    void debeRetornar200_cuandoValidateToken() {
        // Arrange
        ValidacionTokenResult validationResult =
                new ValidacionTokenResult(
                        true,
                        "uuid-estudiante-123",
                        "estudiante@uco.edu.co",
                        "Token valido"
                );

        when(validateTokenInteractor.ejecutar(any())).thenReturn(validationResult);

        // Act
        ResponseEntity<ValidateTokenResponseDTO> response = adapter.validateToken("eyJhbGc-token-valido...");

        // Assert
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValido()).isTrue();
        assertThat(response.getBody().getIdentidadId()).isEqualTo("uuid-estudiante-123");
        assertThat(response.getBody().getCorreo()).isEqualTo("estudiante@uco.edu.co");
        verify(validateTokenInteractor).ejecutar(any(TokenDomain.class));
    }
}
