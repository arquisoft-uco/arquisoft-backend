package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.evaluaciones.evaluacion;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.interactor.RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor;
import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.RegistrarResultadoEvaluacionEntregableProyectoGradoCommand;
import com.arquisoft.entregables.application.entregableproyectogrado.command.result.RegistroResultadoEvaluacionEntregableProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluacionFinalizadaConsumerTest {

    private static final String ID_EVENTO = "evt-evaluacion-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T10:00:00Z");

    @Mock
    private RegistrarResultadoEvaluacionEntregableProyectoGradoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EvaluacionFinalizadaConsumer consumer;

    private final UUID entregable = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new EvaluacionFinalizadaConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        lenient().when(interactor.ejecutar(any())).thenReturn(
                new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Registrado(entregable, "APROBADO"));
    }

    private Message mensaje(String resultado, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "entregableId": "%s",
                    "resultado": "%s",
                    "campoIgnorado": "x"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, entregable, resultado);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorConElEstadoTraducidoYConfirmar_cuandoElResultadoEsAprobada() throws Exception {
        // Act
        consumer.onEvaluacionFinalizada(mensaje("APROBADA", 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RegistrarResultadoEvaluacionEntregableProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
                new RegistrarResultadoEvaluacionEntregableProyectoGradoCommand(entregable, "APROBADO"));
        verify(logger).info(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_RECIBIDO,
                ID_EVENTO, entregable.toString(), "APROBADA");
        verify(logger).info(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_REGISTRADO, entregable, "APROBADO");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarElDescarteYConfirmar_cuandoElInteractorDevuelveDescartado() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(
                new RegistroResultadoEvaluacionEntregableProyectoGradoResult.Descartado(
                        entregable, "APROBADO", "FINALIZADO"));

        // Act
        consumer.onEvaluacionFinalizada(mensaje("RECHAZADA", 2L, false), channel);

        // Assert
        verify(logger).info(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_DESCARTADO,
                entregable, "APROBADO", "FINALIZADO");
        verify(logger, never()).info(eq(EntregableProyectoGradoKey.LOG_RESULTADO_EVALUACION_REGISTRADO), any(), any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElResultadoEsDesconocido() throws Exception {
        // Act
        consumer.onEvaluacionFinalizada(mensaje("PENDIENTE", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEvaluacionFinalizada(mensaje("APROBADA", 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloEsTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onEvaluacionFinalizada(mensaje("APROBADA", 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
        verify(channel, never()).basicAck(5L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloNoEsTransitorio() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("envenenado")).when(interactor).ejecutar(any());

        // Act
        consumer.onEvaluacionFinalizada(mensaje("APROBADA", 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
