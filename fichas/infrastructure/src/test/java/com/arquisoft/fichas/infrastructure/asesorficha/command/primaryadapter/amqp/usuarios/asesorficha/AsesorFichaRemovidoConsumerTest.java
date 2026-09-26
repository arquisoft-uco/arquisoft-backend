package com.arquisoft.fichas.infrastructure.asesorficha.command.primaryadapter.amqp.usuarios.asesorficha;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.interactor.RemoverAsesorFichaInteractor;
import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
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
class AsesorFichaRemovidoConsumerTest {

    private static final String OCURRIDO_EN = "2026-09-24T10:00:00Z";

    @Mock
    private RemoverAsesorFichaInteractor removerAsesorFichaInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private AsesorFichaRemovidoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorFichaRemovidoConsumer(
                removerAsesorFichaInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(removerAsesorFichaInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorFichaResult.Removida(UUID.randomUUID()));
    }

    private Message mensajeCon(UUID usuario, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "1036950123",
                    "nombre": "Laura Gomez",
                    "email": "laura@uco.edu.co"
                }
                """.formatted(UUID.randomUUID(), OCURRIDO_EN, usuario);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractorConElPayloadYConfirmar_cuandoElResultadoEsRemovida() throws Exception {
        // Arrange
        var usuario = UUID.randomUUID();
        when(removerAsesorFichaInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorFichaResult.Removida(usuario));

        // Act
        adapter.onAsesorFichaRemovido(mensajeCon(usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverAsesorFichaCommand.class);
        verify(removerAsesorFichaInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("1036950123");
        assertThat(captor.getValue().nombre()).isEqualTo("Laura Gomez");
        assertThat(captor.getValue().email()).isEqualTo("laura@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse(OCURRIDO_EN));
        verify(logger).info(AsesorFichaKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLaLapidaYConfirmar_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        var usuario = UUID.randomUUID();
        when(removerAsesorFichaInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorFichaResult.Lapida(usuario));

        // Act
        adapter.onAsesorFichaRemovido(mensajeCon(usuario, 2L, false), channel);

        // Assert
        verify(logger).info(AsesorFichaKey.LOG_LAPIDA, usuario);
        verify(logger, never()).info(eq(AsesorFichaKey.LOG_REMOVIDO), any());
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarSinInfoDeCierre_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var usuario = UUID.randomUUID();
        var vigente = Instant.parse("2026-09-25T10:00:00Z");
        when(removerAsesorFichaInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorFichaResult.Descartada(usuario, vigente));

        // Act
        adapter.onAsesorFichaRemovido(mensajeCon(usuario, 3L, false), channel);

        // Assert
        verify(logger).debug(AsesorFichaKey.LOG_REMOCION_DESCARTADA, usuario, vigente);
        verify(logger, never()).info(any(ClaveMensaje.class), eq(usuario));
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerAsesorFichaInteractor).ejecutar(any());

        // Act
        adapter.onAsesorFichaRemovido(mensajeCon(UUID.randomUUID(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerAsesorFichaInteractor).ejecutar(any());

        // Act
        adapter.onAsesorFichaRemovido(mensajeCon(UUID.randomUUID(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(removerAsesorFichaInteractor).ejecutar(any());

        // Act
        adapter.onAsesorFichaRemovido(mensajeCon(UUID.randomUUID(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
