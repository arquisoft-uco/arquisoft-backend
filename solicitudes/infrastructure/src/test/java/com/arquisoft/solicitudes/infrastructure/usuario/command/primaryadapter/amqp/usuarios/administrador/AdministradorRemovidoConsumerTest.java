package com.arquisoft.solicitudes.infrastructure.usuario.command.primaryadapter.amqp.usuarios.administrador;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.solicitudes.UsuarioReplicaKey;
import com.arquisoft.shared.tracing.application.traza.primaryport.impl.GestorTrazaImpl;
import com.arquisoft.shared.tracing.infrastructure.traza.secondaryadapter.mdc.MdcContextoDiagnosticoOutputAdapter;
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

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AdministradorRemovidoConsumerTest {

    @Mock
    private Channel channel;

    @Mock
    private AppLogger logger;

    private AdministradorRemovidoConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new AdministradorRemovidoConsumer(
                new ObjectMapper(),
                logger,
                new GestorTrazaImpl(new MdcContextoDiagnosticoOutputAdapter(), false));
    }

    private Message mensajeCon(String idEvento, String usuario, long deliveryTag) {
        var payloadJson = """
                {
                    "idEvento": "%s",
                    "ocurridoEn": "2026-09-28T10:00:00Z",
                    "usuario": "%s",
                    "identificador": "20161020123",
                    "nombre": "Ana Administradora",
                    "email": "ana@uco.edu.co"
                }
                """.formatted(idEvento, usuario);

        var props = new MessageProperties();
        props.setDeliveryTag(deliveryTag);
        props.setHeader("X-Trace-Id", "trace-123");
        props.setHeader("X-User-Id", "user-456");

        return MessageBuilder.withBody(payloadJson.getBytes()).andProperties(props).build();
    }

    @Test
    void debeLoguearYConfirmar_cuandoLlegaElEvento() throws Exception {
        // Arrange
        var idEvento = UUID.randomUUID().toString();
        var usuario = UUID.randomUUID().toString();

        // Act
        consumer.onAdministradorRemovido(mensajeCon(idEvento, usuario, 1L), channel);

        // Assert
        verify(logger).info(UsuarioReplicaKey.LOG_ADMINISTRADOR_REMOVIDO_RECIBIDO_STUB, idEvento, usuario);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void debeEnviarNackSinReencolar_cuandoElPayloadEsEnvenenado() throws Exception {
        // Arrange
        var mensajeMalformado = MessageBuilder.withBody("{ no-es-json".getBytes())
                .andProperties(mensajeCon("x", "y", 2L).getMessageProperties())
                .build();

        // Act
        consumer.onAdministradorRemovido(mensajeMalformado, channel);

        // Assert
        verify(channel).basicNack(2L, false, false);
    }
}
