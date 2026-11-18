package com.arquisoft.artefactos.infrastructure.proyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.interactor.DescongelarProyectoGradoInteractor;
import com.arquisoft.artefactos.application.proyectogrado.command.primaryport.model.DescongelarProyectoGradoCommand;
import com.arquisoft.artefactos.application.proyectogrado.command.result.DescongelacionProyectoGradoResult;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.shared.util.UtilUUID;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.dao.QueryTimeoutException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntregableProyectoGradoCanceladoConsumerTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-11-08T10:00:00Z");

    @Mock
    private DescongelarProyectoGradoInteractor descongelarProyectoGradoInteractor;
    @Mock
    private Channel channel;
    @Mock
    private AppLogger logger;

    private EntregableProyectoGradoCanceladoConsumer consumer;

    private final UUID proyecto = UtilUUID.generarNuevoUUID();

    @BeforeEach
    void setUp() {
        consumer = new EntregableProyectoGradoCanceladoConsumer(
                descongelarProyectoGradoInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensaje(String proyectoGrado, long deliveryTag, boolean reentregado) {
        var json = """
                {
                    "idEvento": "evt-cancelado-1",
                    "ocurridoEn": "%s",
                    "entregableProyectoGrado": "%s",
                    "proyectoGrado": "%s",
                    "version": 2
                }
                """.formatted(OCURRIDO_EN, UtilUUID.generarNuevoUUID(), proyectoGrado);
        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setRedelivered(reentregado);
        return MessageBuilder.withBody(json.getBytes()).andProperties(props).build();
    }

    @Test
    void debeDescongelarYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        when(descongelarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new DescongelacionProyectoGradoResult.Descongelado(proyecto));

        // Act
        consumer.onEntregableProyectoGradoCancelado(mensaje(proyecto.toString(), 1L, false), channel);

        // Assert
        verify(descongelarProyectoGradoInteractor)
                .ejecutar(new DescongelarProyectoGradoCommand(proyecto, OCURRIDO_EN));
        verify(logger).info(ProyectoGradoKey.LOG_DESCONGELADO, proyecto);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeRegistrarOmitidoYConfirmar_cuandoNoHabiaNadaQueDescongelar() throws Exception {
        // Arrange
        when(descongelarProyectoGradoInteractor.ejecutar(any()))
                .thenReturn(new DescongelacionProyectoGradoResult.Omitido(proyecto));

        // Act
        consumer.onEntregableProyectoGradoCancelado(mensaje(proyecto.toString(), 2L, true), channel);

        // Assert
        verify(logger).info(ProyectoGradoKey.LOG_OMITIDO_DESCONGELAR, proyecto);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void debeApartarADlq_cuandoElPayloadEsInvalido() throws Exception {
        // Act
        consumer.onEntregableProyectoGradoCancelado(mensaje("no-uuid", 3L, false), channel);

        // Assert
        verify(descongelarProyectoGradoInteractor, never()).ejecutar(any());
        verify(channel).basicNack(3L, false, false);
    }

    @Test
    void debeReencolar_cuandoFallaTransitorioEnLaPrimeraEntrega() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(descongelarProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoCancelado(mensaje(proyecto.toString(), 4L, false), channel);

        // Assert
        verify(channel).basicNack(4L, false, true);
    }

    @Test
    void debeEnviarADlq_cuandoFallaTransitorioYaReentregado() throws Exception {
        // Arrange
        doThrow(new QueryTimeoutException("timeout")).when(descongelarProyectoGradoInteractor).ejecutar(any());

        // Act
        consumer.onEntregableProyectoGradoCancelado(mensaje(proyecto.toString(), 5L, true), channel);

        // Assert
        verify(channel).basicNack(5L, false, false);
    }
}
