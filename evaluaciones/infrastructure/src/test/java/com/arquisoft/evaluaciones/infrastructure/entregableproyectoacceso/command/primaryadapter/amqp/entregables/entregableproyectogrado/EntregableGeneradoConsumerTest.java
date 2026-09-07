package com.arquisoft.evaluaciones.infrastructure.entregableproyectoacceso.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.interactor.SincronizarEntregableProyectoAccesoInteractor;
import com.arquisoft.evaluaciones.application.entregableproyectoacceso.command.primaryport.model.SincronizarEntregableProyectoAccesoCommand;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EntregableGeneradoConsumerTest {

    @Mock
    private SincronizarEntregableProyectoAccesoInteractor sincronizarEntregableProyectoAccesoInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EntregableGeneradoConsumer adapter;

    private void crearAdapter() {
        adapter = new EntregableGeneradoConsumer(
                sincronizarEntregableProyectoAccesoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensajeCon(String entregableId, String proyectoId, long deliveryTag, boolean redelivered) {
        String payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-09-06T10:00:00Z",
                    "entregableId": "%s",
                    "proyectoId": "%s",
                    "versionEntregable": 1
                }
                """.formatted(UUID.randomUUID(), entregableId, proyectoId);

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(redelivered);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeSincronizarYConfirmarElMensaje_cuandoElPayloadEsValido() throws Exception {
        // Arrange
        crearAdapter();
        UUID entregable = UUID.randomUUID();
        UUID proyecto = UUID.randomUUID();

        // Act
        adapter.onEntregableGenerado(mensajeCon(entregable.toString(), proyecto.toString(), 1L, false), channel);

        // Assert
        ArgumentCaptor<SincronizarEntregableProyectoAccesoCommand> captor =
                ArgumentCaptor.forClass(SincronizarEntregableProyectoAccesoCommand.class);
        verify(sincronizarEntregableProyectoAccesoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregable()).isEqualTo(entregable);
        assertThat(captor.getValue().proyecto()).isEqualTo(proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadTraeUnEntregableIdMalformado() throws Exception {
        // Arrange — un id que no es UUID hace que el Command.crear() rechace el mensaje: es
        // un mensaje envenenado, no un fallo transitorio, así que va directo a la DLQ.
        crearAdapter();

        // Act
        adapter.onEntregableGenerado(mensajeCon("no-es-un-uuid", UUID.randomUUID().toString(), 2L, false), channel);

        // Assert
        verify(sincronizarEntregableProyectoAccesoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
    }
}
