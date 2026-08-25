package com.arquisoft.seguridad.application.auth.command.usecase.impl;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.seguridad.application.auth.command.secondaryport.TokenInvalidadoOutputPort;
import com.arquisoft.seguridad.domain.auth.SesionDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CerrarSesionUseCaseImplTest {

    @Mock
    private TokenInvalidadoOutputPort tokenInvalidadoOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private CerrarSesionUseCaseImpl cerrarSesionUseCase;

    @Test
    void debeInvalidarToken_cuandoSesionEsValida() {
        // Arrange
        SesionDomain sesion = SesionDomain.crear("jti-123", 120L);

        // Act
        cerrarSesionUseCase.ejecutar(sesion);

        // Assert
        verify(tokenInvalidadoOutputPort).invalidarToken("jti-123", 120L);
    }

    @Test
    void noDebeInvalidarNada_cuandoElTokenYaExpiro() {
        // Arrange — logout idempotente: un token vencido ya no sirve, no hay nada que revocar
        SesionDomain sesion = SesionDomain.crear("jti-123", 0L);

        // Act
        cerrarSesionUseCase.ejecutar(sesion);

        // Assert
        verify(tokenInvalidadoOutputPort, never()).invalidarToken(anyString(), anyLong());
    }
}
