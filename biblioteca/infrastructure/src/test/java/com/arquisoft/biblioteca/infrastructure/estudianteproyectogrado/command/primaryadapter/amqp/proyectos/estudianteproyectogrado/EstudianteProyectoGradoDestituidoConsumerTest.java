package com.arquisoft.biblioteca.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.interactor.RemoverEstudianteProyectoGradoInteractor;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EstudianteProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteProyectoGradoDestituidoConsumerTest {

    private static final String ID_EVENTO = "evt-destituido-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-15T10:00:00Z");

    @Mock
    private RemoverEstudianteProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EstudianteProyectoGradoDestituidoConsumer consumer;

    private UUID proyecto;
    private UUID estudiante;

    @BeforeEach
    void setUp() {
        consumer = new EstudianteProyectoGradoDestituidoConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        proyecto = UtilUUID.generarNuevoUUID();
        estudiante = UtilUUID.generarNuevoUUID();
    }

    private Message mensaje(String estudiantePayload, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "estudianteId": "%s"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyecto, estudiantePayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractorConElPayloadYConfirmar_cuandoElResultadoEsRemovida() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteProyectoGradoResult.Removida(proyecto, estudiante));

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverEstudianteProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(RemoverEstudianteProyectoGradoCommand.crear(
                proyecto.toString(), estudiante.toString(), OCURRIDO_EN));
        verify(logger).info(EstudianteProyectoGradoKey.LOG_REMOVIDO, proyecto, estudiante);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLapidaYConfirmar_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteProyectoGradoResult.Lapida(proyecto, estudiante));

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 2L), channel);

        // Assert
        verify(logger).info(EstudianteProyectoGradoKey.LOG_LAPIDA, proyecto, estudiante);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsEnvenenado() throws Exception {
        // Act
        consumer.onEstudianteDestituido(mensaje("no-es-un-uuid", 3L), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }
}
