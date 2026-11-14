package com.arquisoft.entregables.infrastructure.artefacto.command.primaryadapter.amqp.artefactos.artefacto;

import com.arquisoft.entregables.application.artefacto.command.primaryport.interactor.ReactivarArtefactoInteractor;
import com.arquisoft.entregables.application.artefacto.command.primaryport.model.ReactivarArtefactoCommand;
import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
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
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtefactoReactivadoConsumerTest {

    @Mock
    private ReactivarArtefactoInteractor interactor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private ArtefactoReactivadoConsumer consumer;

    private final UUID artefacto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new ArtefactoReactivadoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(long deliveryTag) {
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
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeReactivarConElArtefactoYElInstanteDelEventoYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new ReactivacionArtefactoResult.Reactivada(artefacto));

        // Act
        consumer.onArtefactoReactivado(mensaje(1L), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(ReactivarArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().artefacto()).isEqualTo(artefacto);
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse("2026-11-10T10:00:00Z"));
        verify(logger).info(ArtefactoKey.LOG_REACTIVADO, artefacto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("invalido")).when(interactor).ejecutar(any());

        // Act
        consumer.onArtefactoReactivado(mensaje(2L), channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }

    @Test
    void debeConfirmar_cuandoSeGuardoLaLapidaAnticipada() throws Exception {
        // Arrange
        when(interactor.ejecutar(any())).thenReturn(new ReactivacionArtefactoResult.Anticipada(artefacto));

        // Act
        consumer.onArtefactoReactivado(mensaje(3L), channel);

        // Assert
        verify(logger).info(ArtefactoKey.LOG_LAPIDA_REGISTRADA, artefacto);
        verify(channel).basicAck(3L, false);
    }
}
