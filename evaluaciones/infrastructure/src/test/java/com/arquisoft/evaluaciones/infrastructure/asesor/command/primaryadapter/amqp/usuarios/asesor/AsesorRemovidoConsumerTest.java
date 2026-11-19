package com.arquisoft.evaluaciones.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import com.arquisoft.evaluaciones.application.asesor.command.primaryport.interactor.RemoverAsesorInteractor;
import com.arquisoft.evaluaciones.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.evaluaciones.application.asesor.command.result.RemocionAsesorResult;
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
class AsesorRemovidoConsumerTest {

    private static final String ID_EVENTO = "evt-asesor-removido-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-12T10:00:00Z");

    @Mock
    private RemoverAsesorInteractor removerAsesorInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private AsesorRemovidoConsumer consumer;

    private UUID usuario;

    @BeforeEach
    void setUp() {
        consumer = new AsesorRemovidoConsumer(
                removerAsesorInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        usuario = UtilUUID.generarNuevoUUID();
        lenient().when(removerAsesorInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorResult.Removida(usuario));
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
        consumer.onAsesorRemovido(mensaje(usuario.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverAsesorCommand.class);
        verify(removerAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new RemoverAsesorCommand(
                usuario, "20161020123", "Ana Perez", "ana@uco.edu.co", OCURRIDO_EN));
        verify(logger).info(eq(AsesorKey.LOG_REMOVIDO_RECIBIDO), eq(ID_EVENTO), eq(usuario.toString()));
        verify(logger).info(AsesorKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarYLoguearLapida() throws Exception {
        // Arrange
        when(removerAsesorInteractor.ejecutar(any())).thenReturn(new RemocionAsesorResult.Lapida(usuario));

        // Act
        consumer.onAsesorRemovido(mensaje(usuario.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(AsesorKey.LOG_LAPIDA, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarYLoguearDescartada() throws Exception {
        // Arrange
        var ocurridoEnVigente = OCURRIDO_EN.plusSeconds(3600);
        when(removerAsesorInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        consumer.onAsesorRemovido(mensaje(usuario.toString(), 3L, false), channel);

        // Assert
        verify(logger).debug(AsesorKey.LOG_REMOCION_DESCARTADA, usuario, ocurridoEnVigente);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeRechazarSinReencolar_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onAsesorRemovido(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(removerAsesorInteractor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
        verify(channel, never()).basicAck(4L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerAsesorInteractor).ejecutar(any());

        // Act
        consumer.onAsesorRemovido(mensaje(usuario.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeRechazarSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerAsesorInteractor).ejecutar(any());

        // Act
        consumer.onAsesorRemovido(mensaje(usuario.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
