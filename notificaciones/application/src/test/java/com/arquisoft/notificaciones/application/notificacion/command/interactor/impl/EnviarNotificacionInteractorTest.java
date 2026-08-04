package com.arquisoft.notificaciones.application.notificacion.command.interactor.impl;

import com.arquisoft.notificaciones.application.notificacion.command.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacionInteractorTest {

    @Mock
    private EnviarNotificacionUseCase enviarNotificacionUseCase;

    @InjectMocks
    private EnviarNotificacionInteractorImpl enviarNotificacionInteractor;

    @Test
    void debeDelegarEnElCasoDeUso_cuandoSeEjecuta() {
        // Arrange
        var command = new EnviarNotificacionCommand(
                "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f",
                TipoNotificacion.ASESOR_FICHA_CAMBIADO,
                "Ana Gomez",
                "ana.gomez@soyuco.edu.co",
                "Asunto",
                "Cuerpo");

        // Act
        enviarNotificacionInteractor.ejecutar(command);

        // Assert — el interactor solo delimita la transaccion y delega
        verify(enviarNotificacionUseCase).ejecutar(command);
    }
}
