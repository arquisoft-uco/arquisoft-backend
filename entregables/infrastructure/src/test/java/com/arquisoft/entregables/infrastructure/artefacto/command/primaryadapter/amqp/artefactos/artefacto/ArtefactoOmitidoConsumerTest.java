package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.OmitirArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtefactoOmitidoConsumerTest {

    @Mock
    private OmitirArtefactoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ArtefactoOmitidoConsumer consumer;

    private final UUID artefacto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ArtefactoOmitidoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "evt-1",
                    "ocurridoEn": "2026-11-10T10:00:00Z",
                    "artefacto": "%s",
                    "proyectoGrado": "%s"
                }
                """.formatted(artefacto, UtilUUID.generarNuevoUUID());
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeOmitirConElArtefactoYElInstanteDelEventoYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new OmisionArtefactoResult.Omitida(artefacto));

        // Act
        consumer.onArtefactoOmitido(mensaje(1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(OmitirArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse("2026-11-10T10:00:00Z"));
        verify(logger).info(ArtefactoKey.LOG_OMITIDO, artefacto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmar_cuandoSeGuardoLaLapidaAnticipada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new OmisionArtefactoResult.Anticipada(artefacto));

        // Act
        consumer.onArtefactoOmitido(mensaje(5L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(OmitirArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().proyectoGrado()).isNotNull();
        verify(logger).info(ArtefactoKey.LOG_LAPIDA_REGISTRADA, artefacto);
        verify(channel).basicAck(5L, false);
    }

    @Test
    void debeConfirmar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new OmisionArtefactoResult.Descartada(artefacto));

        // Act
        consumer.onArtefactoOmitido(mensaje(2L, false), channel);

        // Assert
        verify(logger).info(ArtefactoKey.LOG_DESCARTADA, artefacto);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFallaLaBaseEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onArtefactoOmitido(mensaje(3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarALaDlq_cuandoFallaLaBaseEnLaReentrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onArtefactoOmitido(mensaje(4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }
}
