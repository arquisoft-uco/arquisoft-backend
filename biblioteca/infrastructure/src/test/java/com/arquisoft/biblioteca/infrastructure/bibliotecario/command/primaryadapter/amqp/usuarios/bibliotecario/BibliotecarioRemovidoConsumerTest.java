package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.primaryadapter.amqp.usuarios.bibliotecario;

import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.interactor.RemoverBibliotecarioInteractor;
import com.arquisoft.biblioteca.application.bibliotecario.command.primaryport.model.RemoverBibliotecarioCommand;
import com.arquisoft.biblioteca.application.bibliotecario.command.result.RemocionBibliotecarioResult;
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
class BibliotecarioRemovidoConsumerTest {

    private static final String ID_EVENTO = "evt-bibliotecario-removido-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-09-24T10:00:00Z");

    @Mock
    private RemoverBibliotecarioInteractor removerBibliotecarioInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private BibliotecarioRemovidoConsumer consumer;

    private UUID usuario;

    @BeforeEach
    void setUp() {
        consumer = new BibliotecarioRemovidoConsumer(
                removerBibliotecarioInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        usuario = UtilUUID.generarNuevoUUID();
        lenient().when(removerBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new RemocionBibliotecarioResult.Removida(usuario));
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
    void debeInvocarConElCommandDelPayloadRegistrarRemovidoYConfirmar_cuandoElResultadoEsRemovida()
            throws Exception {
        // Act
        consumer.onBibliotecarioRemovido(mensaje(usuario.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverBibliotecarioCommand.class);
        verify(removerBibliotecarioInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new RemoverBibliotecarioCommand(
                usuario, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN));
        verify(logger).info(eq(BibliotecarioKey.LOG_REMOVIDO_RECIBIDO), eq(ID_EVENTO), eq(usuario.toString()));
        verify(logger).info(BibliotecarioKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLapidaYConfirmar_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        when(removerBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new RemocionBibliotecarioResult.Lapida(usuario));

        // Act
        consumer.onBibliotecarioRemovido(mensaje(usuario.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(BibliotecarioKey.LOG_LAPIDA, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRegistrarDescartadaYConfirmar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var ocurridoEnVigente = OCURRIDO_EN.plusSeconds(3600);
        when(removerBibliotecarioInteractor.ejecutar(any()))
                .thenReturn(new RemocionBibliotecarioResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        consumer.onBibliotecarioRemovido(mensaje(usuario.toString(), 3L, false), channel);

        // Assert
        verify(logger).debug(BibliotecarioKey.LOG_REMOCION_DESCARTADA, usuario, ocurridoEnVigente);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeEnviarNackSinReencolarNiInvocarElInteractor_cuandoElPayloadEsEnvenenado() throws Exception {
        // Act
        consumer.onBibliotecarioRemovido(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(removerBibliotecarioInteractor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
        verify(channel, never()).basicAck(4L, false);
    }

    @Test
    void debeEnviarNackReencolando_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerBibliotecarioInteractor).ejecutar(any());

        // Act
        consumer.onBibliotecarioRemovido(mensaje(usuario.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerBibliotecarioInteractor).ejecutar(any());

        // Act
        consumer.onBibliotecarioRemovido(mensaje(usuario.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
