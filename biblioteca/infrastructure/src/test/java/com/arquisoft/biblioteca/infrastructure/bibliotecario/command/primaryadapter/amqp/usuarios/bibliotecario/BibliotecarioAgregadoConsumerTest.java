package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.primaryadapter.amqp.usuarios.bibliotecario;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.AgregarBibliotecarioInteractor;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.AgregarBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.AgregacionBibliotecarioResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
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
class BibliotecarioAgregadoConsumerTest {

    private static final String ID_EVENTO = "evt-bibliotecario-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private AgregarBibliotecarioInteractor agregarBibliotecarioInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private BibliotecarioAgregadoConsumer consumer;

    private UUID usuario;

    @BeforeEach
    void setUp() {
        consumer = new BibliotecarioAgregadoConsumer(
                agregarBibliotecarioInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        usuario = UtilUUID.generarNuevoUUID();
        lenient().when(agregarBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new AgregacionBibliotecarioResult.Agregada(usuario));
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
        consumer.onBibliotecarioAgregado(mensaje(usuario.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarBibliotecarioCommand.class);
        verify(agregarBibliotecarioInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarBibliotecarioCommand(
                usuario, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN));
        verify(logger).info(eq(BibliotecarioKey.LOG_AGREGADO_RECIBIDO), eq(ID_EVENTO), eq(usuario.toString()),
                eq("a***@uco.edu.co"));
        verify(logger).info(BibliotecarioKey.LOG_AGREGADO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarReactivadoYConfirmar_cuandoElResultadoEsReactivada() throws Exception {
        // Arrange
        when(agregarBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new AgregacionBibliotecarioResult.Reactivada(usuario));

        // Act
        consumer.onBibliotecarioAgregado(mensaje(usuario.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(BibliotecarioKey.LOG_REACTIVADO, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarDuplicadoYConfirmar_cuandoElResultadoEsDuplicada() throws Exception {
        // Arrange
        when(agregarBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new AgregacionBibliotecarioResult.Duplicada(usuario));

        // Act
        consumer.onBibliotecarioAgregado(mensaje(usuario.toString(), 3L, false), channel);

        // Assert
        verify(logger).info(BibliotecarioKey.LOG_DUPLICADO, usuario);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeRegistrarDescartadoYConfirmar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var ocurridoEnVigente = OCURRIDO_EN.plusSeconds(3600);
        when(agregarBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new AgregacionBibliotecarioResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        consumer.onBibliotecarioAgregado(mensaje(usuario.toString(), 4L, false), channel);

        // Assert
        verify(logger).debug(BibliotecarioKey.LOG_DESCARTADO, usuario, ocurridoEnVigente);
        verify(channel).basicAck(4L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsEnvenenado() throws Exception {
        // Act
        consumer.onBibliotecarioAgregado(mensaje("no-es-un-uuid", 5L, false), channel);

        // Assert
        verify(agregarBibliotecarioInteractor, never()).ejecutar(any());
        verify(channel).basicNack(5L, false, false);
        verify(channel, never()).basicAck(5L, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarBibliotecarioInteractor).ejecutar(any());

        // Act
        consumer.onBibliotecarioAgregado(mensaje(usuario.toString(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarBibliotecarioInteractor).ejecutar(any());

        // Act
        consumer.onBibliotecarioAgregado(mensaje(usuario.toString(), 7L, true), channel);

        // Assert
        verify(channel).basicNack(7L, false, false);
    }
}
