package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.artefactos.versionrepositorioartefacto;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.EliminarVersionRepositorioArtefactoInteractor;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.primaryport.model.EliminarVersionRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.infrastructure.config.RepositorioArtefactosArtefactosQueueConfig;
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
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
    @Mock
    private AmqpTemplate amqpTemplate;

    private VersionRepositorioArtefactoEliminacionAutorizadaConsumer consumer;

    private UUID version;

    @BeforeEach
    void setUp() {
        consumer = new VersionRepositorioArtefactoEliminacionAutorizadaConsumer(
                eliminarVersionRepositorioArtefactoInteractor,
                new ObjectMapper(),
                amqpTemplate,
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

    private Message mensajeConReintentos(long deliveryTag, int reintentos) {
        var mensaje = mensaje(version.toString(), deliveryTag, false);
        mensaje.getMessageProperties().setHeader("x-reintentos-eliminacion", reintentos);
        return mensaje;
    }

    private void fallaTransitorio() {
        doThrow(new QueryTimeoutException("timeout")).when(eliminarVersionRepositorioArtefactoInteractor)
                .ejecutar(any());
    }

    @Test
    void debeProgramarReintentoEnColaDeUnMinuto_cuandoPrimerFalloTransitorio() throws Exception {
        // Arrange
        fallaTransitorio();

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje(version.toString(), 4L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(Message.class);
        verify(amqpTemplate).send(eq(""),
                eq(RepositorioArtefactosArtefactosQueueConfig
                        .VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_1M_QUEUE),
                captor.capture());
        assertThat((Integer) captor.getValue().getMessageProperties().getHeader("x-reintentos-eliminacion"))
                .isEqualTo(1);
        verify(channel).basicAck(4L, false);
        verify(channel, never()).basicNack(eq(4L), eq(false), anyBoolean());
    }

    @Test
    void debeProgramarReintentoEnColaDeCincoMinutos_cuandoSegundoFalloTransitorio() throws Exception {
        // Arrange
        fallaTransitorio();

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensajeConReintentos(5L, 1), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(Message.class);
        verify(amqpTemplate).send(eq(""),
                eq(RepositorioArtefactosArtefactosQueueConfig
                        .VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_5M_QUEUE),
                captor.capture());
        assertThat((Integer) captor.getValue().getMessageProperties().getHeader("x-reintentos-eliminacion"))
                .isEqualTo(2);
        verify(channel).basicAck(5L, false);
    }

    @Test
    void debeEnviarADlq_cuandoSeAgotanLosReintentos() throws Exception {
        // Arrange
        fallaTransitorio();

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensajeConReintentos(6L, 2), channel);

        // Assert
        verify(amqpTemplate, never()).send(any(String.class), any(String.class), any(Message.class));
        verify(channel).basicNack(6L, false, false);
        verify(channel, never()).basicAck(6L, false);
    }

    @Test
    void debeProgramarReintento_cuandoFalloTransitorioYBrokerReentrego() throws Exception {
        // Arrange
        fallaTransitorio();

        // Act
        consumer.onVersionRepositorioArtefactoEliminacionAutorizada(mensaje(version.toString(), 7L, true), channel);

        // Assert
        verify(amqpTemplate).send(eq(""),
                eq(RepositorioArtefactosArtefactosQueueConfig
                        .VERSION_REPOSITORIO_ARTEFACTO_ELIMINACION_AUTORIZADA_ESPERA_1M_QUEUE),
                any(Message.class));
        verify(channel).basicAck(7L, false);
    }
}
