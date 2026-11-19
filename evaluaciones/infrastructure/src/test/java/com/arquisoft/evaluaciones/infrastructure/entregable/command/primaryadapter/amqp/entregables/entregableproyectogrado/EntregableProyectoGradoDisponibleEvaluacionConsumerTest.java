package com.arquisoft.evaluaciones.infrastructure.entregable.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.interactor.GenerarEvaluacionInteractor;
import com.arquisoft.evaluaciones.application.evaluacion.command.primaryport.model.GenerarEvaluacionCommand;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.shared.amqp.RabbitMQConfig;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
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
    private GenerarEvaluacionInteractor generarEvaluacionInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoDisponibleEvaluacionConsumer consumer;

    private UUID entregable;
    private UUID evaluacion;
    private UUID proyectoGrado;
    private UUID asesor;

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoDisponibleEvaluacionConsumer(
                generarEvaluacionInteractor,
                new RabbitMQConfig().rabbitObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        entregable = UtilUUID.generarNuevoUUID();
        evaluacion = UtilUUID.generarNuevoUUID();
        proyectoGrado = UtilUUID.generarNuevoUUID();
        asesor = UtilUUID.generarNuevoUUID();
        lenient().when(generarEvaluacionInteractor.ejecutar(any()))
                .thenReturn(new GeneracionEvaluacionResult.Generada(evaluacion, entregable));
    }

    private Message mensaje(String entregableId, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "nombreProyecto": "Sistema de riego",
                    "version": 2,
                    "asesor": "%s"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, entregableId, proyectoGrado, asesor);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeProcesarYConfirmar_cuandoGenerada() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(GenerarEvaluacionCommand.class);
        verify(generarEvaluacionInteractor).ejecutar(captor.capture());
        var comando = captor.getValue().entregable();
        assertThat(comando.entregable()).isEqualTo(entregable);
        assertThat(comando.proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(comando.proyecto()).isEqualTo("Sistema de riego");
        assertThat(comando.version()).isEqualTo(2);
        assertThat(captor.getValue().asesor()).isEqualTo(asesor);
        verify(logger).info(eq(EntregableKey.LOG_DISPONIBLE_RECIBIDO), eq(ID_EVENTO), eq(entregable.toString()),
                eq(proyectoGrado.toString()), eq(asesor.toString()));
        verify(logger).info(EvaluacionKey.LOG_GENERADA, evaluacion, entregable);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmar_cuandoDuplicada() throws Exception {
        // Arrange
        when(generarEvaluacionInteractor.ejecutar(any()))
                .thenReturn(new GeneracionEvaluacionResult.Duplicada(entregable));

        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(EvaluacionKey.LOG_DUPLICADA, entregable);
        verify(logger, never()).info(eq(EvaluacionKey.LOG_GENERADA), any(), any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarADlqSinReencolar_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(generarEvaluacionInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFalloTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(generarEvaluacionInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(generarEvaluacionInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoDisponibleEvaluacion(mensaje(entregable.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
