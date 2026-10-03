package com.arquisoft.proyectos.infrastructure.coordinador.command.primaryadapter.amqp.usuarios.coordinador;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.interactor.RemoverCoordinadorInteractor;
import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.RemoverCoordinadorCommand;
import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinadorRemovidoConsumerTest {

    private static final String OCURRIDO_EN = "2026-09-24T10:00:00Z";

    @Mock
    private RemoverCoordinadorInteractor removerCoordinadorInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private CoordinadorRemovidoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new CoordinadorRemovidoConsumer(
                removerCoordinadorInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(removerCoordinadorInteractor.ejecutar(any()))
                .thenReturn(new RemocionCoordinadorResult.Removida(UtilUUID.generarNuevoUUID()));
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
                """.formatted(UtilUUID.generarNuevoUUID(), OCURRIDO_EN, usuario);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractorConElPayloadYConfirmar_cuandoElResultadoEsRemovida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(removerCoordinadorInteractor.ejecutar(any())).thenReturn(new RemocionCoordinadorResult.Removida(usuario));

        // Act
        adapter.onCoordinadorRemovido(mensajeCon(usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverCoordinadorCommand.class);
        verify(removerCoordinadorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("20161020123");
        assertThat(captor.getValue().nombre()).isEqualTo("Ana Perez");
        assertThat(captor.getValue().email()).isEqualTo("ana@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse(OCURRIDO_EN));
        verify(logger).info(CoordinadorKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLapidaYConfirmar_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(removerCoordinadorInteractor.ejecutar(any())).thenReturn(new RemocionCoordinadorResult.Lapida(usuario));

        // Act
        adapter.onCoordinadorRemovido(mensajeCon(usuario, 2L, false), channel);

        // Assert
        verify(logger).info(CoordinadorKey.LOG_LAPIDA, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarSinEscalar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var ocurridoEnVigente = Instant.parse("2026-09-25T10:00:00Z");
        when(removerCoordinadorInteractor.ejecutar(any()))
                .thenReturn(new RemocionCoordinadorResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        adapter.onCoordinadorRemovido(mensajeCon(usuario, 3L, false), channel);

        // Assert
        verify(logger).debug(CoordinadorKey.LOG_REMOCION_DESCARTADA, usuario, ocurridoEnVigente);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerCoordinadorInteractor).ejecutar(any());

        // Act
        adapter.onCoordinadorRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerCoordinadorInteractor).ejecutar(any());

        // Act
        adapter.onCoordinadorRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(removerCoordinadorInteractor).ejecutar(any());

        // Act
        adapter.onCoordinadorRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
