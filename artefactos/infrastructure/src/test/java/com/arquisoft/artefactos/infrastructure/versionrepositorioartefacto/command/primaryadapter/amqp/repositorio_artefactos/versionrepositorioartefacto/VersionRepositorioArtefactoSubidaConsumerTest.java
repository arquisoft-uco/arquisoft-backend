package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.versionrepositorioartefacto;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.AgregarVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.AgregarVersionRepositorioArtefactoCommand;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.result.AgregacionVersionRepositorioArtefactoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.AgregarVersionRepositorioArtefactoKey;
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
class VersionRepositorioArtefactoSubidaConsumerTest {

    private static final String ID_EVENTO = "evt-version-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-16T10:00:00Z");

    @Mock
    private AgregarVersionRepositorioArtefactoInteractor agregarVersionRepositorioArtefactoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private VersionRepositorioArtefactoSubidaConsumer consumer;

    private UUID version;
    private UUID repositorio;

    @BeforeEach
    void setUp() {
        consumer = new VersionRepositorioArtefactoSubidaConsumer(
                agregarVersionRepositorioArtefactoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        version = UtilUUID.generarNuevoUUID();
        repositorio = UtilUUID.generarNuevoUUID();
        lenient().when(agregarVersionRepositorioArtefactoInteractor.ejecutar(any()))
                .thenReturn(new AgregacionVersionRepositorioArtefactoResult.Agregada(version));
    }

    private Message mensaje(String versionPayload, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "versionRepositorioArtefacto": "%s",
                    "nombreDocumento": "Plantilla de tesis",
                    "versionDocumento": 3,
                    "repositorioArtefacto": "%s",
                    "nombreRepositorio": "Repositorio base",
                    "nombreArchivo": "plantilla.pdf",
                    "tipoContenido": "application/pdf",
                    "tamanoBytes": 2048
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, versionPayload, repositorio);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeProcesarYConfirmar_cuandoEventoValido() throws Exception {
        // Act
        consumer.onVersionRepositorioArtefactoSubida(mensaje(version.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(AgregarVersionRepositorioArtefactoCommand.class);
        verify(agregarVersionRepositorioArtefactoInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue()).isEqualTo(new AgregarVersionRepositorioArtefactoCommand(
                version, "Plantilla de tesis", 3, repositorio, "Repositorio base",
                new AgregarVersionRepositorioArtefactoCommand.DatosArchivo("plantilla.pdf", "application/pdf", 2048L)));
        verify(logger).info(AgregarVersionRepositorioArtefactoKey.LOG_AGREGADA, version);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmar_cuandoResultadoDuplicada() throws Exception {
        // Arrange
        when(agregarVersionRepositorioArtefactoInteractor.ejecutar(any()))
                .thenReturn(new AgregacionVersionRepositorioArtefactoResult.Duplicada(version));

        // Act
        consumer.onVersionRepositorioArtefactoSubida(mensaje(version.toString(), 2L, false), channel);

        // Assert
        verify(logger).info(AgregarVersionRepositorioArtefactoKey.LOG_DUPLICADA, version);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeLoguearRecepcionYCierre() throws Exception {
        // Act
        consumer.onVersionRepositorioArtefactoSubida(mensaje(version.toString(), 3L, false), channel);

        // Assert
        verify(logger).info(eq(AgregarVersionRepositorioArtefactoKey.LOG_VERSION_RECIBIDA), eq(ID_EVENTO),
                eq(version.toString()), eq(repositorio.toString()), eq(3));
        verify(logger).info(AgregarVersionRepositorioArtefactoKey.LOG_AGREGADA, version);
    }

    @Test
    void debeEnviarADlqSinReencolar_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onVersionRepositorioArtefactoSubida(mensaje("no-es-un-uuid", 4L, false), channel);

        // Assert
        verify(agregarVersionRepositorioArtefactoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(4L, false, false);
        verify(channel, never()).basicAck(4L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFalloTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarVersionRepositorioArtefactoInteractor)
                .ejecutar(any());

        // Act
        consumer.onVersionRepositorioArtefactoSubida(mensaje(version.toString(), 5L, false), channel);

        // Assert
        verify(channel).basicNack(5L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(agregarVersionRepositorioArtefactoInteractor)
                .ejecutar(any());

        // Act
        consumer.onVersionRepositorioArtefactoSubida(mensaje(version.toString(), 6L, true), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
