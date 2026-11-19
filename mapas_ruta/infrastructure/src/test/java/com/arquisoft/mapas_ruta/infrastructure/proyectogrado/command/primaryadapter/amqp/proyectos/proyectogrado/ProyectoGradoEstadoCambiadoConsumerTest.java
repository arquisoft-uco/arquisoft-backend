package com.arquisoft.mapas_ruta.infrastructure.proyectogrado.command.primaryadapter.amqp.proyectos.proyectogrado;

import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.interactor.ActualizarEstadoProyectoGradoInteractor;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.primaryport.model.ActualizarEstadoProyectoGradoCommand;
import com.arquisoft.mapas_ruta.application.proyectogrado.command.result.ActualizacionEstadoProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.ProyectoGradoMapasRutaKey;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoEstadoCambiadoConsumerTest {

    private static final String ID_EVENTO = "evt-est-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-19T05:05:00Z");

    @Mock
    private ActualizarEstadoProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ProyectoGradoEstadoCambiadoConsumer consumer;

    private UUID proyecto;

    @BeforeEach
    void setUp() {
        consumer = new ProyectoGradoEstadoCambiadoConsumer(interactor, new ObjectMapper(), logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        proyecto = UtilUUID.generarNuevoUUID();
        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Actualizado(proyecto, "ATRASADO"));
    }

    private Message mensaje(String proyectoPayload, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "proyectoGradoId": "%s",
                    "estadoNuevo": "ATRASADO"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, proyectoPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractorYLoguearAplicado_cuandoLlegaEvento() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(ActualizarEstadoProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
                new ActualizarEstadoProyectoGradoCommand(proyecto, "ATRASADO", OCURRIDO_EN));
        verify(logger).info(ProyectoGradoMapasRutaKey.LOG_ESTADO_RECIBIDO,
                ID_EVENTO, proyecto.toString(), "ATRASADO", OCURRIDO_EN);
        verify(logger).info(ProyectoGradoMapasRutaKey.LOG_ESTADO_APLICADO, proyecto, "ATRASADO");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeLoguearDescartadoYConfirmar_cuandoElResultadoEsDescartado() throws Exception {
        // Arrange
        var guardado = OCURRIDO_EN.plusSeconds(30);
        when(interactor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoProyectoGradoResult.Descartado(proyecto, guardado));

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(ProyectoGradoMapasRutaKey.LOG_ESTADO_DESCARTADO, proyecto, guardado);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeEnviarADlq_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFalloTransitorioEnPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onProyectoGradoEstadoCambiado(mensaje(proyecto.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
