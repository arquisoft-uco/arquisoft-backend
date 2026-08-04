package com.arquisoft.shared.notification.logging;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.notification.model.NotificationMessage;
import com.arquisoft.shared.notification.model.NotificationRecipient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingNotificationSenderTest {

    @Mock
    private AppLogger logger;

    @InjectMocks
    private LoggingNotificationSender sender;

    @Test
    void debeRegistrarElMensajeSinEntregarlo_cuandoSeInvocaEnviar() {
        // Arrange
        NotificationMessage mensaje = NotificationMessage.textoPlano(
                new NotificationRecipient("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                "Asunto de prueba",
                "Cuerpo de prueba");

        // Act
        sender.enviar(mensaje);

        // Assert — la unica salida de esta estrategia es el log; no hay transporte que verificar
        verify(logger).info(any(String.class), any(), any());
    }

    @Test
    void debeSoportarVariosDestinatarios_cuandoElMensajeLosTrae() {
        // Arrange
        NotificationMessage mensaje = new NotificationMessage(
                List.of(
                        new NotificationRecipient("Ana", "ana@soyuco.edu.co"),
                        new NotificationRecipient("Luis", "luis@soyuco.edu.co")),
                "Asunto",
                "Cuerpo",
                false);

        // Act
        sender.enviar(mensaje);

        // Assert
        verify(logger).info(any(String.class), any(), any());
    }
}
