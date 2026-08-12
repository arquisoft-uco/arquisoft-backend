package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.seguridad.application.auth.command.primaryport.model.TokenSesionCommand;
import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
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
class CerrarSesionUseCaseImplTest {

    @Mock
    private TokenInvalidadoOutputPort tokenInvalidadoOutputPort;

        // Catalogo real, no mock: varios mensajes acaban en la excepcion o en el
    // resultado, y un mock los dejaria en null.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

@InjectMocks
    private CerrarSesionUseCaseImpl cerrarSesionUseCase;

    @Test
    void debeInvalidarToken_cuandoSesionEsValida() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 120L);

        // Act
        cerrarSesionUseCase.ejecutar(command);

        // Assert
        verify(tokenInvalidadoOutputPort).invalidarToken("jti-123", 120L);
    }

    @Test
    void debeLanzarExcepcion_cuandoIdentificadorEsVacio() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("   ", 120L);

        // Act / Assert
        assertThatThrownBy(() -> cerrarSesionUseCase.ejecutar(command))
                .isInstanceOf(DomainException.class);
        verify(tokenInvalidadoOutputPort, never()).invalidarToken(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyLong());
    }

    @Test
    void debeLanzarExcepcion_cuandoTiempoDeVidaNoEsPositivo() {
        // Arrange
        TokenSesionCommand command = new TokenSesionCommand("jti-123", 0L);

        // Act / Assert
        assertThatThrownBy(() -> cerrarSesionUseCase.ejecutar(command))
                .isInstanceOf(DomainException.class);
    }
}
