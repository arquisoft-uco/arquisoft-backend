package com.arquisoft.evaluaciones.infrastructure.entregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.interactor.AgregarEntregableInteractor;
import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;
import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
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
class EntregableProyectoGradoDisponibleEvaluacionConsumerTest {

    private static final String ID_EVENTO = "evt-disponible-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-13T10:00:00Z");

    @Mock
    private AgregarEntregableInteractor agregarEntregableInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoDisponibleEvaluacionConsumer consumer;

    private UUID entregable;
    private UUID proyectoGrado;

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoDisponibleEvaluacionConsumer(
                agregarEntregableInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        entregable = UtilUUID.generarNuevoUUID();
        proyectoGrado = UtilUUID.generarNuevoUUID();
        lenient().when(agregarEntregableInteractor.ejecutar(any()))
                .thenReturn(new AgregacionEntregableResult.Agregado(entregable));
    }

    private Message mensaje(String entregableId, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "nombreProyecto": "Sistema de riego",
                    "version": 2
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, entregableId, proyectoGrado);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeProcesarYConfirmar_cuandoEventoValido() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarEntregableCommand.class);
        verify(agregarEntregableInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
                new AgregarEntregableCommand(entregable, proyectoGrado, "Sistema de riego", 2));
        verify(logger).info(eq(EntregableKey.LOG_DISPONIBLE_RECIBIDO), eq(ID_EVENTO), eq(entregable.toString()),
                eq(proyectoGrado.toString()));
        verify(logger).info(EntregableKey.LOG_AGREGADO, entregable);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmar_cuandoResultadoDuplicado() throws Exception {
        // Arrange
        when(agregarEntregableInteractor.ejecutar(any()))
                .thenReturn(new AgregacionEntregableResult.Duplicado(entregable));

        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(EntregableKey.LOG_DUPLICADO, entregable);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarADlqSinReencolar_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(agregarEntregableInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFalloTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarEntregableInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarEntregableInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
