package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.arquisoft.seguridad.application.auth.command.model.TokenSesionCommand;
import com.arquisoft.seguridad.domain.auth.port.out.TokenBlacklistOutputPort;
import com.arquisoft.shared.exception.DomainException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutUseCaseImplTest {

    @Mock
    private TokenBlacklistOutputPort tokenBlacklistOutputPort;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private MessageCatalog catalog = ResourceBundleMessageCatalog.porDefecto();

@InjectMocks
    private LogoutUseCaseImpl logoutUseCase;

    @Test
    void debeInvalidarToken_cuandoSesionEsValida() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 120L);

        // Act
        logoutUseCase.ejecutar(command);

        // Assert
        verify(tokenBlacklistOutputPort).invalidarToken("jti-123", 120L);
    }

    @Test
    void debeLanzarExcepcion_cuandoIdentificadorEsVacio() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("   ", 120L);

        // Act / Assert
        assertThatThrownBy(() -> logoutUseCase.ejecutar(command))
                .isInstanceOf(DomainException.class);
        verify(tokenBlacklistOutputPort, never()).invalidarToken(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void debeLanzarExcepcion_cuandoTiempoDeVidaNoEsPositivo() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 0L);

        // Act / Assert
        assertThatThrownBy(() -> logoutUseCase.ejecutar(command))
                .isInstanceOf(DomainException.class);
    }
}
