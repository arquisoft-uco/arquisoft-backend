package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.coordinador;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
import com.arquisoft.solicitudes.application.usuario.command.primaryport.interactor.RegistrarUsuarioInteractor;
import com.arquisoft.solicitudes.application.usuario.command.result.AgregacionUsuarioResult;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinadorAgregadoConsumerTest {

    @Mock
    private RegistrarUsuarioInteractor registrarUsuarioInteractor;

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private CoordinadorAgregadoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new CoordinadorAgregadoConsumer(
                registrarUsuarioInteractor,
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));

        lenient().when(registrarUsuarioInteractor.ejecutar(any()))
                .thenReturn(new AgregacionUsuarioResult.Agregada(UUID.randomUUID()));
    }

    private Message mensajeCon(String idEvento, long deliveryTag) {
        String payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-09-12T10:00:00Z",
                    "usuario": "%s",
                    "identificador": "COORD-001",
                    "nombre": "Ana Coordinadora",
                    "email": "ana@uco.edu.co"
                }
                """.formatted(idEvento, UUID.randomUUID());

        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeInvocarElInteractor_cuandoLlegaElEvento() throws Exception {
        // Act
        consumer.onCoordinadorAgregado(mensajeCon(UUID.randomUUID().toString(), 1L), channel);

        // Assert
        verify(registrarUsuarioInteractor).ejecutar(any());
    }

    @Test
    void debeConfirmarElMensaje_cuandoElProcesamientoTermina() throws Exception {
        // Act
        consumer.onCoordinadorAgregado(mensajeCon(UUID.randomUUID().toString(), 1L), channel);

        // Assert
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("id invalido"))
                .when(registrarUsuarioInteractor).ejecutar(any());

        // Act
        consumer.onCoordinadorAgregado(mensajeCon(UUID.randomUUID().toString(), 2L), channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }

    @Test
    void debeConfirmarElMensaje_cuandoElResultadoEsDescartada() throws Exception {
        // Arrange
        when(registrarUsuarioInteractor.ejecutar(any())).thenReturn(
                new AgregacionUsuarioResult.Descartada(UUID.randomUUID(), Instant.now()));

        // Act
        consumer.onCoordinadorAgregado(mensajeCon(UUID.randomUUID().toString(), 3L), channel);

        // Assert
        verify(channel).basicAck(3L, false);
    }
}
