package com.arquisoft.proyectos.infrastructure.asesor.command.primaryadapter.amqp.usuarios.asesor;

import com.arquisoft.proyectos.application.asesor.command.primaryport.interactor.RemoverAsesorInteractor;
import com.arquisoft.proyectos.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
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
class AsesorRemovidoConsumerTest {

    private static final String OCURRIDO_EN = "2026-09-23T10:00:00Z";

    @Mock
    private RemoverAsesorInteractor removerAsesorInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private AsesorRemovidoConsumer adapter;

    @BeforeEach
    void setUp() {
        adapter = new AsesorRemovidoConsumer(
                removerAsesorInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(removerAsesorInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorResult.Removida(UtilUUID.generarNuevoUUID()));
    }

    private Message mensajeCon(UUID usuario, long deliveryTag, boolean reentregado) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "%s",
                    "usuario": "%s",
                    "identificador": "1036950123",
                    "nombre": "Carlos Rios",
                    "email": "carlos@uco.edu.co"
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
        when(removerAsesorInteractor.ejecutar(any())).thenReturn(new RemocionAsesorResult.Removida(usuario));

        // Act
        adapter.onAsesorRemovido(mensajeCon(usuario, 1L, false), channel);

        // Assert
        var captor = ArgumentCaptor.forClass(RemoverAsesorCommand.class);
        verify(removerAsesorInteractor).ejecutar(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(usuario);
        assertThat(captor.getValue().identificador()).isEqualTo("1036950123");
        assertThat(captor.getValue().nombre()).isEqualTo("Carlos Rios");
        assertThat(captor.getValue().email()).isEqualTo("carlos@uco.edu.co");
        assertThat(captor.getValue().ocurridoEn()).isEqualTo(Instant.parse(OCURRIDO_EN));
        verify(logger).info(AsesorKey.LOG_REMOVIDO, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarLapidaYConfirmar_cuandoElResultadoEsLapida() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(removerAsesorInteractor.ejecutar(any())).thenReturn(new RemocionAsesorResult.Lapida(usuario));

        // Act
        adapter.onAsesorRemovido(mensajeCon(usuario, 2L, false), channel);

        // Assert
        verify(logger).info(AsesorKey.LOG_LAPIDA, usuario);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeConfirmarSinEscalar_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var ocurridoEnVigente = Instant.parse("2026-09-24T10:00:00Z");
        when(removerAsesorInteractor.ejecutar(any()))
                .thenReturn(new RemocionAsesorResult.Descartada(usuario, ocurridoEnVigente));

        // Act
        adapter.onAsesorRemovido(mensajeCon(usuario, 3L, false), channel);

        // Assert
        verify(logger).debug(AsesorKey.LOG_REMOCION_DESCARTADA, usuario, ocurridoEnVigente);
        verify(channel).basicAck(3L, false);
    }

    @Test
    void debeReencolar_cuandoElFalloEsTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerAsesorInteractor).ejecutar(any());

        // Act
        adapter.onAsesorRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElFalloTransitorioYaFueReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(removerAsesorInteractor).ejecutar(any());

        // Act
        adapter.onAsesorRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido")).when(removerAsesorInteractor).ejecutar(any());

        // Act
        adapter.onAsesorRemovido(mensajeCon(UtilUUID.generarNuevoUUID(), 6L, false), channel);

        // Assert
        verify(channel).basicNack(6L, false, false);
    }
}
