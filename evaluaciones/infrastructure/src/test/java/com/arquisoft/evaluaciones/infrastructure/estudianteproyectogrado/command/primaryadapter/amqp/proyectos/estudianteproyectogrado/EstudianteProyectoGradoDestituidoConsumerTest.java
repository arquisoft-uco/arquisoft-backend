package com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.primaryadapter.amqp.proyectos.estudianteproyectogrado;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.interactor.RemoverEstudianteProyectoGradoInteractor;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.primaryport.model.RemoverEstudianteProyectoGradoCommand;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstudianteProyectoGradoKey;
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
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteProyectoGradoDestituidoConsumerTest {

    private static final String ID_EVENTO = "evt-destituido-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-21T10:00:00Z");

    @Mock
    private RemoverEstudianteProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EstudianteProyectoGradoDestituidoConsumer consumer;

    private final UUID proyectoGrado = UtilUUID.generarNuevoUUID();
    private final UUID estudiante = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new EstudianteProyectoGradoDestituidoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String estudiantePayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "estudianteId": "%s"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoGrado, estudiantePayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorYRegistrarRemovida_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteProyectoGradoResult.Removida(proyectoGrado, estudiante));

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverEstudianteProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue())
                .isEqualTo(new RemoverEstudianteProyectoGradoCommand(proyectoGrado, estudiante, OCURRIDO_EN));
        verify(logger).info(EstudianteProyectoGradoKey.LOG_DESTITUIDO_RECIBIDO, ID_EVENTO,
                proyectoGrado.toString(), estudiante.toString());
        verify(logger).info(EstudianteProyectoGradoKey.LOG_REMOVIDO, proyectoGrado, estudiante);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLapida_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        when(interactor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteProyectoGradoResult.Lapida(proyectoGrado, estudiante));

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(EstudianteProyectoGradoKey.LOG_LAPIDA, proyectoGrado, estudiante);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarDescartada_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var vigente = OCURRIDO_EN.plusSeconds(60);
        when(interactor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteProyectoGradoResult.Descartada(proyectoGrado, estudiante, vigente));

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 3L, false), channel);

        // Assert
        verify(logger).debug(EstudianteProyectoGradoKey.LOG_REMOCION_DESCARTADA, proyectoGrado, estudiante, vigente);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsMalformado() throws Exception {
        // Act
        consumer.onEstudianteDestituido(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEstudianteDestituido(mensaje(estudiante.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
        verify(channel, never()).basicAck(6L, false);
    }
}
