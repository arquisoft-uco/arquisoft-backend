package com.arquisoft.evaluaciones.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import com.arquisoft.evaluaciones.application.asesor.command.primaryport.interactor.AgregarAsesorInteractor;
import com.arquisoft.evaluaciones.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import com.arquisoft.evaluaciones.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.AsesorKey;
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
class AsesorAgregadoConsumerTest {

    private static final String ID_EVENTO = "evt-asesor-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AgregarAsesorInteractor agregarAsesorInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private AsesorAgregadoConsumer consumer;

    private UUID usuario;

    @BeforeEach
    void setUp() {
        consumer = new AsesorAgregadoConsumer(
                agregarAsesorInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        usuario = UtilUUID.generarNuevoUUID();
        lenient().when(agregarAsesorInteractor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorResult.Agregada(usuario));
    }

    private Message mensaje(String usuarioPayload, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "20161020123",
                    "nombre": "Ana Perez",
                    "email": "ana@uco.edu.co"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, usuarioPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarConElCommandDelPayloadRegistrarAgregadoYConfirmar_cuandoElResultadoEsAgregada()
            throws Exception {
        // Act
        consumer.onAsesorAgregado(mensaje(usuario.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarAsesorCommand.class);
        verify(agregarAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarAsesorCommand(
                usuario, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN));
        verify(logger).info(eq(AsesorKey.LOG_AGREGADO_RECIBIDO), eq(ID_EVENTO), eq(usuario.toString()),
                eq("a***@uco.edu.co"));
        verify(logger).info(AsesorKey.LOG_AGREGADO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarReactivadoYConfirmar_cuandoElResultadoEsReactivada() throws Exception {
        // Arrange
        when(agregarAsesorInteractor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorResult.Reactivada(usuario));

        // Act
        consumer.onAsesorAgregado(mensaje(usuario.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(AsesorKey.LOG_REACTIVADO, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarDuplicadoYConfirmar_cuandoElResultadoEsDuplicada() throws Exception {
        // Arrange
        when(agregarAsesorInteractor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorResult.Duplicada(usuario));

        // Act
        consumer.onAsesorAgregado(mensaje(usuario.toString(), 3L, false), channel);

        // Assert
        verify(logger).info(AsesorKey.LOG_DUPLICADO, usuario);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeRegistrarDescartadoYConfirmar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var ocurridoEnVigente = OCURRIDO_EN.plusSeconds(3600);
        when(agregarAsesorInteractor.ejecutar(any()))
                .thenReturn(new AgregacionAsesorResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        consumer.onAsesorAgregado(mensaje(usuario.toString(), 4L, false), channel);

        // Assert
        verify(logger).debug(AsesorKey.LOG_DESCARTADO, usuario, ocurridoEnVigente);
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsEnvenenado() throws Exception {
        // Act
        consumer.onAsesorAgregado(mensaje("no-es-un-uuid", 5L, false), channel);

        // Assert
        verify(agregarAsesorInteractor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, false);
        verify(channel, never()).basicAck(5L, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarAsesorInteractor).ejecutar(any());

        // Act
        consumer.onAsesorAgregado(mensaje(usuario.toString(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarAsesorInteractor).ejecutar(any());

        // Act
        consumer.onAsesorAgregado(mensaje(usuario.toString(), 7L, true), channel);

        // Assert
        verify(channel).basicNack(7L, false, false);
    }
}
