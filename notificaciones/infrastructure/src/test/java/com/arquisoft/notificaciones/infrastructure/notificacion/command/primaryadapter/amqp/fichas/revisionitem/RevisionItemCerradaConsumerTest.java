package com.arquisoft.notificaciones.infrastructure.notificacion.command.primaryadapter.amqp.fichas.revisionitem;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.notificaciones.ConsumidorKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
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

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RevisionItemCerradaConsumerTest {

    private static final String DOS_ESTUDIANTES = """
            [
                {"nombre": "Ana Gomez", "email": "ana.gomez@soyuco.edu.co"},
                {"nombre": "Luis Diaz", "email": "luis.diaz@soyuco.edu.co"}
            ]""";

    @Mock
    private EnviarNotificacionInteractor enviarNotificacionInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private RevisionItemCerradaConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new RevisionItemCerradaConsumer(
                enviarNotificacionInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(enviarNotificacionInteractor.ejecutar(any()))
                .thenReturn(new EnvioNotificacionResult.Enviada("evt", "ana.gomez@soyuco.edu.co"));
    }

    private Message mensajeCon(String idEvento, String revisionItemId, String estudiantesJson, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "revisionItemId": "%s",
                    "fichaPerfilId": "%s",
                    "tituloProyecto": "Sistema de gestión",
                    "estudiantes": %s
                }
                """.formatted(idEvento, revisionItemId, UUID.randomUUID(), estudiantesJson);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private List<EnviarNotificacionCommand> comandosEmitidos(int esperados) {
        var captor = ArgumentCaptor.forClass(EnviarNotificacionCommand.class);
        verify(enviarNotificacionInteractor, Mockito.times(esperados)).ejecutar(captor.capture());
        return captor.getAllValues();
    }

    @Test
    void debeEnviarUnCorreoPorEstudiante_cuandoElPayloadTraeVarios() throws Exception {
        // Arrange
        var idEvento = UUID.randomUUID().toString();
        var revisionItemId = UUID.randomUUID().toString();

        // Act
        adapter.onRevisionItemCerrada(mensajeCon(idEvento, revisionItemId, DOS_ESTUDIANTES, 1L), channel);

        // Assert
        var comandos = comandosEmitidos(2);
        assertThat(comandos).extracting(EnviarNotificacionCommand::destinatarioEmail)
                .containsExactly("ana.gomez@soyuco.edu.co", "luis.diaz@soyuco.edu.co");
        assertThat(comandos).extracting(EnviarNotificacionCommand::idEvento).containsOnly(idEvento);
        assertThat(comandos).extracting(EnviarNotificacionCommand::tipo)
                .containsOnly(TipoNotificacion.REVISION_ITEM_CERRADA);
        assertThat(comandos.get(0).cuerpo()).contains("Ana Gomez", "Sistema de gestión");
        assertThat(comandos.get(1).cuerpo()).contains("Luis Diaz", "Sistema de gestión");
        verify(logger).info(eq(ConsumidorKey.LOG_REVISION_ITEM_CERRADA_RECIBIDO), eq(revisionItemId), eq(2));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeNoEnviar_cuandoNoHayEstudiantes() throws Exception {
        // Act
        adapter.onRevisionItemCerrada(
                mensajeCon(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "[]", 2L), channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRechazarSinReencolar_cuandoElPayloadEsInvalido() throws Exception {
        // Arrange
        var props = new MessageProperties();
        props.setDeliveryTag(3L);
        var mensaje = MessageBuilder.withBody("no es json".getBytes()).andProperties(props).build();

        // Act
        adapter.onRevisionItemCerrada(mensaje, channel);

        // Assert
        verify(enviarNotificacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
        verify(logger, never()).info(eq(ConsumidorKey.LOG_REVISION_ITEM_CERRADA_RECIBIDO), any(), anyInt());
    }
}
