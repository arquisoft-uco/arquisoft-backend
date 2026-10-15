package com.arquisoft.evaluaciones.infrastructure.jurado.command.primaryadapter.amqp.usuarios.jurado;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.interactor.RemoverJuradoInteractor;
import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
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
class JuradoRemovidoConsumerTest {

    private static final String ID_EVENTO = "evt-jurado-removido-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-12T10:00:00Z");

    @Mock
    private RemoverJuradoInteractor removerJuradoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private JuradoRemovidoConsumer consumer;

    private UUID usuario;

    @BeforeEach
    void setUp() {
        consumer = new JuradoRemovidoConsumer(
                removerJuradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        usuario = UtilUUID.generarNuevoUUID();
        lenient().when(removerJuradoInteractor.ejecutar(any()))
                .thenReturn(new RemocionJuradoResult.Removida(usuario));
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
    void debeConfirmarYLoguearRemovida() throws Exception {
        // Act
        consumer.onJuradoRemovido(mensaje(usuario.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverJuradoCommand.class);
        verify(removerJuradoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new RemoverJuradoCommand(
                usuario, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN));
        verify(logger).info(eq(JuradoKey.LOG_REMOVIDO_RECIBIDO), eq(ID_EVENTO), eq(usuario.toString()));
        verify(logger).info(JuradoKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarYLoguearLapida() throws Exception {
        // Arrange
        when(removerJuradoInteractor.ejecutar(any())).thenReturn(new RemocionJuradoResult.Lapida(usuario));

        // Act
        consumer.onJuradoRemovido(mensaje(usuario.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(JuradoKey.LOG_LAPIDA, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarYLoguearDescartada() throws Exception {
        // Arrange
        var ocurridoEnVigente = OCURRIDO_EN.plusSeconds(3600);
        when(removerJuradoInteractor.ejecutar(any()))
                .thenReturn(new RemocionJuradoResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        consumer.onJuradoRemovido(mensaje(usuario.toString(), 3L, false), channel);

        // Assert
        verify(logger).debug(JuradoKey.LOG_REMOCION_DESCARTADA, usuario, ocurridoEnVigente);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeRechazarSinReencolar_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onJuradoRemovido(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(removerJuradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
        verify(channel, never()).basicAck(4L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerJuradoInteractor).ejecutar(any());

        // Act
        consumer.onJuradoRemovido(mensaje(usuario.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeRechazarSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerJuradoInteractor).ejecutar(any());

        // Act
        consumer.onJuradoRemovido(mensaje(usuario.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
