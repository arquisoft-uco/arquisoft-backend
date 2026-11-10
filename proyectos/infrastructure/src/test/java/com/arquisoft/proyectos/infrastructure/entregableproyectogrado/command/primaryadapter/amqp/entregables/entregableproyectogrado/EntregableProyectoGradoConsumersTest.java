package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.interactor.ActualizarEstadoEntregableProyectoGradoInteractor;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model.ActualizarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EntregableProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
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

import java.io.IOException;
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
class EntregableProyectoGradoConsumersTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-01T10:00:00Z");

    @Mock
    private ActualizarEstadoEntregableProyectoGradoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoGeneradoConsumer generado;
    private EntregableProyectoGradoCerradoConsumer cerrado;
    private EntregableProyectoGradoCanceladoConsumer cancelado;
    private EntregableProyectoGradoRechazadoConsumer rechazado;

    private final UUID entregable = UUID.randomUUID();
    private final UUID proyectoGrado = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        var mapper = new ObjectMapper();
        var gestorTraza = new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false);
        generado = new EntregableProyectoGradoGeneradoConsumer(interactor, mapper, logger, gestorTraza);
        cerrado = new EntregableProyectoGradoCerradoConsumer(interactor, mapper, logger, gestorTraza);
        cancelado = new EntregableProyectoGradoCanceladoConsumer(interactor, mapper, logger, gestorTraza);
        rechazado = new EntregableProyectoGradoRechazadoConsumer(interactor, mapper, logger, gestorTraza);

        lenient().when(interactor.ejecutar(any()))
                .thenReturn(new ActualizacionEstadoEntregableProyectoGradoResult.Actualizado(entregable, "X"));
    }

    private Message mensaje(long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-11-01T10:00:00Z",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "version": 2
                }
                """.formatted(entregable, proyectoGrado);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        props.setHeader("X-Trace-Id", "trace-123");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    private void verificarCommand(String estadoEsperado) {
        var captor = ArgumentCaptor.forClass(ActualizarEstadoEntregableProyectoGradoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(captor.getValue().proyectoGrado()).isEqualTo(proyectoGrado);
        assertThat(captor.getValue().version()).isEqualTo(2);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(OCURRIDO_EN);
        assertThat(captor.getValue().estadoEntregable()).isEqualTo(estadoEsperado);
    }

    @Test
    void debeActualizarConDisponibleEvaluacionYConfirmar_cuandoLlegaGenerado() throws IOException {
        // Act
        generado.onEntregableProyectoGradoGenerado(mensaje(1L, false), channel);

        // Assert
        verificarCommand("DISPONIBLE_EVALUACION");
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeActualizarConCerradoYConfirmar_cuandoLlegaCerrado() throws IOException {
        // Act
        cerrado.onEntregableProyectoGradoCerrado(mensaje(2L, false), channel);

        // Assert
        verificarCommand("CERRADO");
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeActualizarConCanceladoYConfirmar_cuandoLlegaCancelado() throws IOException {
        // Act
        cancelado.onEntregableProyectoGradoCancelado(mensaje(3L, false), channel);

        // Assert
        verificarCommand("CANCELADO");
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeActualizarConRechazadoYConfirmar_cuandoLlegaRechazado() throws IOException {
        // Act
        rechazado.onEntregableProyectoGradoRechazado(mensaje(8L, false), channel);

        // Assert
        verificarCommand("RECHAZADO");
        verify(channel).basicAck(8L, false);
    }

    @Test
    void debeConfirmarYRegistrarDescarte_cuandoElEventoEsMasViejo() throws IOException {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(
                new ActualizacionEstadoEntregableProyectoGradoResult.Descartado(entregable, OCURRIDO_EN));

        // Act
        cerrado.onEntregableProyectoGradoCerrado(mensaje(4L, false), channel);

        // Assert
        verify(logger).debug(EntregableProyectoGradoKey.LOG_DESCARTADO, entregable, OCURRIDO_EN);
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeReencolarUnaVezYLuegoEnviarALaDlq_cuandoFallaLaBase() throws IOException {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        cerrado.onEntregableProyectoGradoCerrado(mensaje(5L, false), channel);
        cerrado.onEntregableProyectoGradoCerrado(mensaje(6L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
        verify(channel).basicNack(6L, false, false);
    }

    @Test
    void debeEnviarALaDlqSinReintentar_cuandoElMensajeEsEnvenenado() throws IOException {
        // Arrange
        doThrow(new IllegalArgumentException("payload invalido")).when(interactor).ejecutar(any());

        // Act
        generado.onEntregableProyectoGradoGenerado(mensaje(7L, false), channel);

        // Assert
        verify(channel).basicNack(7L, false, false);
        verify(logger, never())
                .info(eq(EntregableProyectoGradoKey.LOG_ACTUALIZADO), any(), any());
    }
}
