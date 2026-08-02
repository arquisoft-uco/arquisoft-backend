package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.seguridad.application.auth.command.result.RefrescoTokenResult;
import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AuthenticationOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenUseCaseImplTest {

    @Mock
    private AuthenticationOutputPort authenticationOutputPort;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private RefreshTokenUseCaseImpl refreshTokenUseCase;

    @Test
    void debeRetornarNuevasCredenciales_cuandoRefrescoEsExitoso() {
        // Arrange
        when(authenticationOutputPort.refrescar("refresh-viejo"))
                .thenReturn(CredencialesSesion.de("access-nuevo", "refresh-nuevo", 600L, "Bearer", "openid"));

        // Act
        RefrescoTokenResult resultado = refreshTokenUseCase.ejecutar("refresh-viejo");

        // Assert
        assertThat(resultado.accessToken()).isEqualTo("access-nuevo");
        assertThat(resultado.refreshToken()).isEqualTo("refresh-nuevo");
        assertThat(resultado.expiresIn()).isEqualTo(600L);
        assertThat(resultado.tokenType()).isEqualTo("Bearer");
        assertThat(resultado.scope()).isEqualTo("openid");
    }

    @Test
    void debeRefrescarUnaSolaVez_cuandoEjecutar() {
        // Arrange — el refresh token rota en Keycloak: refrescar dos veces invalidaria el token
        when(authenticationOutputPort.refrescar("refresh-viejo"))
                .thenReturn(CredencialesSesion.de("a", "r", 60L, "Bearer", ""));

        // Act
        refreshTokenUseCase.ejecutar("refresh-viejo");

        // Assert
        verify(authenticationOutputPort).refrescar("refresh-viejo");
        verify(authenticationOutputPort, org.mockito.Mockito.never()).validarTokenRefresco("refresh-viejo");
    }

    @Test
    void debePropagarExcepcion_cuandoTokenEstaExpirado() {
        // Arrange
        when(authenticationOutputPort.refrescar("expirado"))
                .thenThrow(new AuthenticationException("Refresh token invalido o expirado"));

        // Act / Assert
        assertThatThrownBy(() -> refreshTokenUseCase.ejecutar("expirado"))
                .isInstanceOf(AuthenticationException.class);
    }
}
