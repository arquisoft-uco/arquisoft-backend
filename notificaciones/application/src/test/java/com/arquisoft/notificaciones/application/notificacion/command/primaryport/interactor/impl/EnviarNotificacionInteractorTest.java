package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.impl;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacionInteractorTest {

    @Mock
    private EnviarNotificacionUseCase enviarNotificacionUseCase;

    @InjectMocks
    private EnviarNotificacionInteractorImpl enviarNotificacionInteractor;

    @Test
    void debeMapearElComandoADominioYDelegar_cuandoSeEjecuta() {
        // Arrange
        var command = EnviarNotificacionCommand.crear(
                "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f",
                TipoNotificacion.ASESOR_FICHA_CAMBIADO.getId(),
                "Ana Gomez",
                "ana.gomez@soyuco.edu.co",
                "Asunto",
                "Cuerpo");

        // Act
        enviarNotificacionInteractor.ejecutar(command);

        // Assert — el interactor delimita la transaccion, mapea a dominio y delega
        ArgumentCaptor<NotificacionDomain> captor =
                ArgumentCaptor.forClass(NotificacionDomain.class);
        verify(enviarNotificacionUseCase).ejecutar(captor.capture());

        NotificacionDomain envio = captor.getValue();
        assertThat(envio.getIdEvento()).isEqualTo("8f14e45f-ceea-467a-9575-1a1b2c3d4e5f");
        assertThat(envio.getDestinatario()).isEqualTo("ana.gomez@soyuco.edu.co");
        assertThat(envio.getDestinatarioNombre()).isEqualTo("Ana Gomez");
        assertThat(envio.getCuerpo()).isEqualTo("Cuerpo");
        assertThat(envio.getTipo())
                .isEqualTo(TipoNotificacion.ASESOR_FICHA_CAMBIADO);
    }
}
