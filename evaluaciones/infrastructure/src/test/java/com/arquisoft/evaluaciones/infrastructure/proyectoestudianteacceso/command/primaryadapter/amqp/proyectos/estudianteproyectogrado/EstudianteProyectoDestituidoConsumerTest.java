package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.interactor.SincronizarProyectoEstudianteAccesoInteractor;
import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model.SincronizarProyectoEstudianteAccesoCommand;
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
class EstudianteProyectoDestituidoConsumerTest {

    @Mock
    private SincronizarProyectoEstudianteAccesoInteractor sincronizarProyectoEstudianteAccesoInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EstudianteProyectoDestituidoConsumer adapter;

    private void crearAdapter() {
        adapter = new EstudianteProyectoDestituidoConsumer(
                sincronizarProyectoEstudianteAccesoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensajeCon(String proyectoId, String estudianteId, long deliveryTag) {
        String payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-09-06T10:00:00Z",
                    "proyectoId": "%s",
                    "estudianteId": "%s"
                }
                """.formatted(UUID.randomUUID(), proyectoId, estudianteId);

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeSincronizarComoDestitucionInactiva_yConfirmarElMensaje() throws Exception {
        // Arrange
        crearAdapter();
        UUID proyecto = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();

        // Act
        adapter.onEstudianteProyectoDestituido(mensajeCon(proyecto.toString(), estudiante.toString(), 1L), channel);

        // Assert
        ArgumentCaptor<SincronizarProyectoEstudianteAccesoCommand> captor =
                ArgumentCaptor.forClass(SincronizarProyectoEstudianteAccesoCommand.class);
        verify(sincronizarProyectoEstudianteAccesoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyecto()).isEqualTo(proyecto);
        assertThat(captor.getValue().estudiante()).isEqualTo(estudiante);
        assertThat(captor.getValue().activo()).isFalse();
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadTraeUnProyectoIdMalformado() throws Exception {
        // Arrange
        crearAdapter();

        // Act
        adapter.onEstudianteProyectoDestituido(
                mensajeCon("no-es-un-uuid", UUID.randomUUID().toString(), 2L), channel);

        // Assert
        verify(sincronizarProyectoEstudianteAccesoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
    }
}
