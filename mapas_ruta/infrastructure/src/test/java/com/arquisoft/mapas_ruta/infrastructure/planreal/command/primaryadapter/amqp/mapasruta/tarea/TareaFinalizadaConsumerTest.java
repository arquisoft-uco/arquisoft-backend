package com.arquisoft.mapas_ruta.infrastructure.planreal.command.primaryadapter.amqp.mapasruta.tarea;

import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.interactor.GenerarPlanRealInteractor;
import com.arquisoft.mapas_ruta.application.planreal.command.primaryport.model.GenerarPlanRealCommand;
import com.arquisoft.mapas_ruta.application.planreal.command.result.GeneracionPlanRealResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.PlanRealKey;
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
class TareaFinalizadaConsumerTest {

    private static final String ID_EVENTO = "evt-tarea-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-05T10:00:00Z");

    @Mock
    private GenerarPlanRealInteractor generarPlanRealInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private TareaFinalizadaConsumer consumer;

    private UUID tarea;

    @BeforeEach
    void setUp() {
        consumer = new TareaFinalizadaConsumer(
                generarPlanRealInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        tarea = UtilUUID.generarNuevoUUID();
        lenient().when(generarPlanRealInteractor.ejecutar(any()))
                .thenReturn(new GeneracionPlanRealResult.Generada(tarea, 2));
    }

    private Message mensaje(String tareaPayload, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "tarea": "%s"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, tareaPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractor_cuandoLlegaEvento() throws Exception {
        // Act
        consumer.onTareaFinalizada(mensaje(tarea.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(GenerarPlanRealCommand.class);
        verify(generarPlanRealInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new GenerarPlanRealCommand(tarea, OCURRIDO_EN));
        verify(logger).info(PlanRealKey.LOG_TAREA_FINALIZADA_RECIBIDA, ID_EVENTO, tarea.toString());
        verify(logger).info(PlanRealKey.LOG_GENERADO, tarea, 2);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarRecalculadoYConfirmar_cuandoElResultadoEsRecalculada() throws Exception {
        // Arrange
        when(generarPlanRealInteractor.ejecutar(any()))
                .thenReturn(new GeneracionPlanRealResult.Recalculada(tarea, 3));

        // Act
        consumer.onTareaFinalizada(mensaje(tarea.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(PlanRealKey.LOG_RECALCULADO, tarea, 3);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarDescartadoYConfirmar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(generarPlanRealInteractor.ejecutar(any()))
                .thenReturn(new GeneracionPlanRealResult.Descartada(tarea));

        // Act
        consumer.onTareaFinalizada(mensaje(tarea.toString(), 3L, false), channel);

        // Assert
        verify(logger).info(PlanRealKey.LOG_DESCARTADO, tarea);
        verify(channel).basicAck(3L, false);
        verify(channel, never()).basicNack(eq(3L), any(Boolean.class), any(Boolean.class));
    }

    @Test
    void debeEnviarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onTareaFinalizada(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(generarPlanRealInteractor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
        verify(channel, never()).basicAck(4L, false);
    }

    @Test
    void debeReencolar_cuandoFalloTransitorioEnPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(generarPlanRealInteractor).ejecutar(any());

        // Act
        consumer.onTareaFinalizada(mensaje(tarea.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(generarPlanRealInteractor).ejecutar(any());

        // Act
        consumer.onTareaFinalizada(mensaje(tarea.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
