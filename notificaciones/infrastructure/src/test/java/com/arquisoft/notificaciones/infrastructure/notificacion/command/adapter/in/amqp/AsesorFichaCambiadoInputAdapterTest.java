package com.arquisoft.notificaciones.infrastructure.notificacion.command.adapter.in.amqp;

import com.arquisoft.notificaciones.application.notificacion.command.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ResourceBundleMessageCatalog;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsesorFichaCambiadoInputAdapterTest {

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    private AsesorFichaCambiadoInputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorFichaCambiadoInputAdapter(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                Mockito.mock(AppLogger.class),
                ResourceBundleMessageCatalog.porDefecto());
    }

    private Message mensajeCon(String eventId, long deliveryTag) {
        String payloadJson = """
                {
                    "eventId": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "asesorNombre": "Ana Gomez",
                    "asesorEmail": "ana.gomez@soyuco.edu.co"
                }
                """.formatted(eventId, UUID.randomUUID());

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeTraducirElEventoEnUnaNotificacion_cuandoElPayloadEsValido() throws Exception {
        // Arrange
        String eventId = UUID.randomUUID().toString();

        // Act
        adapter.onAsesorFichaCambiado(mensajeCon(eventId, 1L), channel);

        // Assert
        ArgumentCaptor<EnviarNotificacionCommand> captor =
                ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());

        EnviarNotificacionCommand command = captor.getValue();
        assertThat(command.eventId()).isEqualTo(eventId);
        assertThat(command.tipo()).isEqualTo(TipoNotificacion.ASESOR_FICHA_CAMBIADO);
        assertThat(command.destinatarioNombre()).isEqualTo("Ana Gomez");
        assertThat(command.destinatarioEmail()).isEqualTo("ana.gomez@soyuco.edu.co");
    }

    @Test
    void debeResolverElAsuntoYElCuerpoDesdeElCatalogo_cuandoConstruyeElComando() throws Exception {
        // Act
        adapter.onAsesorFichaCambiado(mensajeCon(UUID.randomUUID().toString(), 1L), channel);

        // Assert — los textos salen del bundle, no de literales en el adapter
        ArgumentCaptor<EnviarNotificacionCommand> captor =
                ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor).ejecutar(captor.capture());

        assertThat(captor.getValue().asunto()).contains("Sistema de gestión");
        assertThat(captor.getValue().cuerpo())
                .contains("Ana Gomez")
                .contains("Sistema de gestión");
    }

    @Test
    void debeConfirmarElMensaje_cuandoElProcesamientoTermina() throws Exception {
        // Act
        adapter.onAsesorFichaCambiado(mensajeCon(UUID.randomUUID().toString(), 1L), channel);

        // Assert
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElInteractorFalla() throws Exception {
        // Arrange — requeue=false manda el mensaje a la DLX en lugar de reintentarlo en bucle
        doThrow(new RuntimeException("fallo al notificar"))
                .when(enviarNotificacionInteractor).ejecutar(any());

        // Act
        adapter.onAsesorFichaCambiado(mensajeCon(UUID.randomUUID().toString(), 2L), channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }
}
