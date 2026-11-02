package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.artefactos.versionrepositorioartefacto;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.EliminarVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
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
class VersionRepositorioArtefactoEliminacionAutorizadaConsumerTest {

    private static final String ID_EVENTO = "evt-eliminacion-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-02T10:00:00Z");

    @Mock
    private EliminarVersionRepositorioArtefactoInteractor eliminarVersionRepositorioArtefactoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private VersionRepositorioArtefactoEliminacionAutorizadaConsumer consumer;

    private UUID version;

    @BeforeEach
    void setUp() {
        consumer = new VersionRepositorioArtefactoEliminacionAutorizadaConsumer(
                eliminarVersionRepositorioArtefactoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        version = UtilUUID.generarNuevoUUID();
        lenient().when(eliminarVersionRepositorioArtefactoInteractor.ejecutar(any()))
                .thenReturn(new EliminacionVersionRepositorioArtefactoResult.Eliminada(version));
    }

    private Message mensaje(String versionPayload, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "versionRepositorioArtefacto": "%s",
                    "nombreDocumento": "Formato A",
                    "versionDocumento": 2,
                    "repositorioArtefacto": "%s"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, versionPayload, UtilUUID.generarNuevoUUID());
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractor_cuandoLlegaElEvento() throws Exception {
        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje(version.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(EliminarVersionRepositorioArtefactoCommand.class);
        verify(eliminarVersionRepositorioArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().version()).isEqualTo(version);
        verify(logger).info(VersionRepositorioArtefactoKey.LOG_ELIMINACION_RECIBIDA, ID_EVENTO, version.toString());
        verify(logger).info(VersionRepositorioArtefactoKey.LOG_VERSION_ELIMINADA, version);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmar_cuandoLaVersionYaNoExiste() throws Exception {
        // Arrange
        when(eliminarVersionRepositorioArtefactoInteractor.ejecutar(any()))
                .thenReturn(new EliminacionVersionRepositorioArtefactoResult.Inexistente(version));

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje(version.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(VersionRepositorioArtefactoKey.LOG_VERSION_INEXISTENTE, version);
        verify(logger, never()).info(eq(VersionRepositorioArtefactoKey.LOG_VERSION_ELIMINADA), eq(version));
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeRechazar_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje("no-es-un-uuid", 3L, false), channel);

        // Assert
        verify(eliminarVersionRepositorioArtefactoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(3L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFalloTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(eliminarVersionRepositorioArtefactoInteractor)
                .ejecutar(any());

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje(version.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(eliminarVersionRepositorioArtefactoInteractor)
                .ejecutar(any());

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje(version.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
