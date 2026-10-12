package com.arquisoft.fichas.infrastructure.estudiante.command.primaryadapter.amqp.usuarios.estudiante;

import com.arquisoft.fichas.application.estudiante.command.primaryport.interactor.RemoverEstudianteInteractor;
import com.arquisoft.fichas.application.estudiante.command.primaryport.model.RemoverEstudianteCommand;
import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.shared.logger.AppLogger;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudianteRemovidoConsumerTest {

    private static final String OCURRIDO_EN = "2026-09-16T10:00:00Z";

    @Mock
    private RemoverEstudianteInteractor removerEstudianteInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private EstudianteRemovidoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new EstudianteRemovidoConsumer(
                removerEstudianteInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(removerEstudianteInteractor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteResult.Removida(UUID.randomUUID()));
    }

    private Message mensajeCon(UUID usuario, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "20161020123",
                    "nombre": "Ana Perez",
                    "email": "ana@uco.edu.co"
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

        // Act
        adapter.onEstudianteRemovido(mensajeCon(usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverEstudianteCommand.class);
        verify(removerEstudianteInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().email()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse(OCURRIDO_EN));
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        when(removerEstudianteInteractor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteResult.Lapida(UUID.randomUUID()));

        // Act
        adapter.onEstudianteRemovido(mensajeCon(UUID.randomUUID(), 2L, false), channel);

        // Assert
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(removerEstudianteInteractor.ejecutar(any()))
                .thenReturn(new RemocionEstudianteResult.Descartada(UUID.randomUUID(), Instant.now()));

        // Act
        adapter.onEstudianteRemovido(mensajeCon(UUID.randomUUID(), 3L, false), channel);

        // Assert
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onEstudianteRemovido(mensajeCon(UUID.randomUUID(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onEstudianteRemovido(mensajeCon(UUID.randomUUID(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(removerEstudianteInteractor).ejecutar(any());

        // Act
        adapter.onEstudianteRemovido(mensajeCon(UUID.randomUUID(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
