package com.arquisoft.artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.amqp.repositorio_artefactos.repositorioartefacto;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.interactor.CambiarNombreRepositorioVersionRepositorioArtefactoInteractor;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.primaryport.model.CambiarNombreRepositorioVersionRepositorioArtefactoCommand;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarNombreRepositorioVersionRepositorioArtefactoKey;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RepositorioArtefactoModificadoConsumerTest {

    private static final String ID_EVENTO = "evt-repo-modificado-1";
    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-23T10:00:00Z");

    @Mock
    private CambiarNombreRepositorioVersionRepositorioArtefactoInteractor interactor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private RepositorioArtefactoModificadoConsumer consumer;

    private UUID repositorio;

    @BeforeEach
    void setUp() {
        consumer = new RepositorioArtefactoModificadoConsumer(
                interactor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
        repositorio = UtilUUID.generarNuevoUUID();
    }

    private Message mensaje(String repositorioPayload, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "repositorioArtefacto": "%s",
                    "nombreRepositorio": "Repositorio nuevo"
                }
                """.formatted(ID_EVENTO, OCURRIDO_EN, repositorioPayload);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarInteractor_cuandoLlegaEvento() throws Exception {
        // Act
        consumer.onRepositorioArtefactoModificado(mensaje(repositorio.toString(), 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(CambiarNombreRepositorioVersionRepositorioArtefactoCommand.class);
        verify(interactor).ejecutar(captor.capture());
        assertThat(captor.getValue().repositorioArtefacto()).isEqualTo(repositorio);
        assertThat(captor.getValue().nombreRepositorio()).isEqualTo("Repositorio nuevo");
        verify(logger).info(CambiarNombreRepositorioVersionRepositorioArtefactoKey.LOG_REPOSITORIO_MODIFICADO_RECIBIDO,
                ID_EVENTO, repositorio.toString());
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRechazarComoEnvenenado_cuandoPayloadInvalido() throws Exception {
        // Act
        consumer.onRepositorioArtefactoModificado(mensaje("no-es-un-uuid", 2L, false), channel);

        // Assert
        verify(interactor, never()).ejecutar(any());
        verify(channel).basicNack(2L, false, false);
        verify(channel, never()).basicAck(2L, false);
    }

    @Test
    void debeReencolarUnaVez_cuandoFalloTransitorio() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onRepositorioArtefactoModificado(mensaje(repositorio.toString(), 3L, false), channel);

        // Assert
        verify(channel).basicNack(3L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFalloTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(interactor).ejecutar(any());

        // Act
        consumer.onRepositorioArtefactoModificado(mensaje(repositorio.toString(), 4L, true), channel);

        // Assert
        verify(channel).basicNack(4L, false, false);
    }
}
