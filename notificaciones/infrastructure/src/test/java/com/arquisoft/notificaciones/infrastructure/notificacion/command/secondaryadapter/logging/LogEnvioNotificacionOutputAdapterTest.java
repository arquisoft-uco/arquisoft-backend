package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.logging;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogEnvioNotificacionOutputAdapterTest {

    @Mock
    private AppLogger logger;

    @InjectMocks
    private LogEnvioNotificacionOutputAdapter sender;

    @Test
    void debeRegistrarElMensajeSinEntregarlo_cuandoSeInvocaEnviar() {
        // Arrange
        MensajeNotificacion mensaje = MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                "Asunto de prueba",
                "Cuerpo de prueba",
                "Pie de prueba");

        // Act
        sender.enviar(mensaje);

        // Assert
        verify(logger).info(any(String.class), any(), any());
    }

    @Test
    void debeSoportarVariosDestinatarios_cuandoElMensajeLosTrae() {
        // Arrange
        MensajeNotificacion mensaje = new MensajeNotificacion(
                List.of(
                        new DestinatarioNotificacion("Ana", "ana@soyuco.edu.co"),
                        new DestinatarioNotificacion("Luis", "luis@soyuco.edu.co")),
                "Asunto",
                "Cuerpo",
                "Pie");

        // Act
        sender.enviar(mensaje);

        // Assert
        verify(logger).info(any(String.class), any(), any());
    }
}
